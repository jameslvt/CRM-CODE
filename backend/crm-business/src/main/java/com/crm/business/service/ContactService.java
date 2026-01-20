package com.crm.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.business.dto.ContactDTO;
import com.crm.business.dto.ContactFormData;
import com.crm.business.entity.Contact;
import com.crm.business.entity.Customer;
import com.crm.business.mapper.ContactMapper;
import com.crm.business.mapper.CustomerMapper;
import com.crm.common.exception.BusinessException;
import com.crm.common.result.PageResult;
import com.crm.common.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 联系人服务
 * 提供联系人的CRUD、设置主要联系人等功能
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactMapper contactMapper;
    private final CustomerMapper customerMapper;
    private final IdGenerator idGenerator;

    /**
     * 分页查询联系人列表
     *
     * @param customerId 客户ID
     * @param name 联系人姓名（模糊查询）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public PageResult<ContactDTO> getContactList(Long customerId, String name, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Contact> wrapper = new LambdaQueryWrapper<>();

        // 客户ID必填
        wrapper.eq(Contact::getCustomerId, customerId);

        // 姓名模糊查询
        if (StringUtils.hasText(name)) {
            wrapper.like(Contact::getName, name);
        }

        // 按主要联系人优先，然后按创建时间倒序
        wrapper.orderByDesc(Contact::getIsPrimary)
               .orderByDesc(Contact::getCreateTime);

        // 分页查询
        Page<Contact> page = new Page<>(pageNum, pageSize);
        IPage<Contact> contactPage = contactMapper.selectPage(page, wrapper);

        // 转换为DTO
        List<ContactDTO> dtoList = contactPage.getRecords().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

        return new PageResult<>(
            dtoList,
            contactPage.getTotal(),
            contactPage.getCurrent(),
            contactPage.getSize()
        );
    }

    /**
     * 获取客户的所有联系人
     *
     * @param customerId 客户ID
     * @return 联系人列表
     */
    public List<ContactDTO> getContactsByCustomerId(Long customerId) {
        LambdaQueryWrapper<Contact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contact::getCustomerId, customerId)
               .orderByDesc(Contact::getIsPrimary)
               .orderByDesc(Contact::getCreateTime);

        List<Contact> contacts = contactMapper.selectList(wrapper);
        return contacts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 根据ID获取联系人详情
     *
     * @param id 联系人ID
     * @return 联系人DTO
     */
    public ContactDTO getContactById(Long id) {
        Contact contact = contactMapper.selectById(id);
        if (contact == null) {
            throw new BusinessException("联系人不存在");
        }
        return convertToDTO(contact);
    }

    /**
     * 创建联系人
     *
     * @param formData 表单数据
     * @return 联系人ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createContact(ContactFormData formData) {
        // 验证客户是否存在
        Customer customer = customerMapper.selectById(formData.getCustomerId());
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        Contact contact = new Contact();
        BeanUtils.copyProperties(formData, contact);
        contact.setId(idGenerator.nextId());

        // 处理主要联系人标记
        if (formData.getIsPrimary() != null && formData.getIsPrimary()) {
            contact.setIsPrimary(1);
            // 取消其他主要联系人
            cancelOtherPrimaryContacts(formData.getCustomerId(), null);
        } else {
            contact.setIsPrimary(0);
        }

        contactMapper.insert(contact);
        log.info("创建联系人成功，ID: {}, 客户ID: {}", contact.getId(), formData.getCustomerId());
        return contact.getId();
    }

    /**
     * 更新联系人
     *
     * @param id 联系人ID
     * @param formData 表单数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateContact(Long id, ContactFormData formData) {
        Contact existingContact = contactMapper.selectById(id);
        if (existingContact == null) {
            throw new BusinessException("联系人不存在");
        }

        Contact contact = new Contact();
        BeanUtils.copyProperties(formData, contact);
        contact.setId(id);
        contact.setCustomerId(existingContact.getCustomerId()); // 不允许修改客户ID

        // 处理主要联系人标记
        if (formData.getIsPrimary() != null && formData.getIsPrimary()) {
            contact.setIsPrimary(1);
            // 取消其他主要联系人
            cancelOtherPrimaryContacts(existingContact.getCustomerId(), id);
        } else {
            contact.setIsPrimary(0);
        }

        contactMapper.updateById(contact);
        log.info("更新联系人成功，ID: {}", id);
    }

    /**
     * 删除联系人
     *
     * @param id 联系人ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteContact(Long id) {
        Contact contact = contactMapper.selectById(id);
        if (contact == null) {
            throw new BusinessException("联系人不存在");
        }

        contactMapper.deleteById(id);
        log.info("删除联系人成功，ID: {}", id);
    }

    /**
     * 设置为主要联系人
     *
     * @param id 联系人ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void setPrimaryContact(Long id) {
        Contact contact = contactMapper.selectById(id);
        if (contact == null) {
            throw new BusinessException("联系人不存在");
        }

        // 取消其他主要联系人
        cancelOtherPrimaryContacts(contact.getCustomerId(), id);

        // 设置当前联系人为主要联系人
        contact.setIsPrimary(1);
        contactMapper.updateById(contact);

        log.info("设置主要联系人成功，ID: {}, 客户ID: {}", id, contact.getCustomerId());
    }

    /**
     * 取消其他主要联系人
     *
     * @param customerId 客户ID
     * @param excludeId 排除的联系人ID
     */
    private void cancelOtherPrimaryContacts(Long customerId, Long excludeId) {
        LambdaQueryWrapper<Contact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contact::getCustomerId, customerId)
               .eq(Contact::getIsPrimary, 1);

        if (excludeId != null) {
            wrapper.ne(Contact::getId, excludeId);
        }

        List<Contact> primaryContacts = contactMapper.selectList(wrapper);
        for (Contact c : primaryContacts) {
            c.setIsPrimary(0);
            contactMapper.updateById(c);
        }
    }

    /**
     * 转换为DTO
     */
    private ContactDTO convertToDTO(Contact contact) {
        ContactDTO dto = new ContactDTO();
        BeanUtils.copyProperties(contact, dto);

        // 设置性别名称
        if (contact.getGender() != null) {
            dto.setGenderName(contact.getGender() == 1 ? "男" : "女");
        }

        // TODO: 查询客户名称（需要关联客户表）

        return dto;
    }
}
