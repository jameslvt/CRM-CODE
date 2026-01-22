package com.crm.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.business.dto.*;
import com.crm.business.entity.Contact;
import com.crm.business.entity.Customer;
import com.crm.business.mapper.ContactMapper;
import com.crm.business.mapper.CustomerMapper;
import com.crm.common.exception.BusinessException;
import com.crm.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户服务
 * 提供客户的CRUD、公海池、360度视图等功能
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerMapper customerMapper;
    private final ContactMapper contactMapper;

    /**
     * 客户状态映射
     */
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();

    /**
     * 客户编码前缀
     */
    private static final String CUSTOMER_CODE_PREFIX = "CUS";

    static {
        STATUS_MAP.put(1, "正常");
        STATUS_MAP.put(2, "公海");
    }

    /**
     * 分页查询客户列表
     *
     * @param params 查询参数
     * @return 分页结果
     */
    public PageResult<CustomerDTO> getCustomerList(CustomerQueryParams params) {
        LambdaQueryWrapper<Customer> wrapper = buildQueryWrapper(params);

        // 分页查询
        Page<Customer> page = new Page<>(params.getPageNum(), params.getPageSize());
        IPage<Customer> customerPage = customerMapper.selectPage(page, wrapper);

        // 转换为DTO
        List<CustomerDTO> dtoList = customerPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResult<>(
                dtoList,
                customerPage.getTotal(),
                customerPage.getCurrent(),
                customerPage.getSize());
    }

    /**
     * 根据ID获取客户详情
     *
     * @param id 客户ID
     * @return 客户DTO
     */
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        return convertToDTO(customer);
    }

    /**
     * 获取客户360度视图
     *
     * @param id 客户ID
     * @return 360度视图DTO
     */
    public Customer360DTO getCustomer360(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        Customer360DTO dto = new Customer360DTO();

        // 基本信息
        dto.setBasicInfo(convertToDTO(customer));

        // 联系人列表
        LambdaQueryWrapper<Contact> contactWrapper = new LambdaQueryWrapper<>();
        contactWrapper.eq(Contact::getCustomerId, id)
                .orderByDesc(Contact::getIsPrimary)
                .orderByDesc(Contact::getCreateTime);
        List<Contact> contacts = contactMapper.selectList(contactWrapper);
        dto.setContacts(contacts.stream()
                .map(this::convertContactToDTO)
                .collect(Collectors.toList()));

        // TODO: 商机列表（需要商机模块实现后补充）
        // TODO: 合同列表（需要合同模块实现后补充）
        // TODO: 跟进记录列表（需要跟进记录模块实现后补充）

        // 统计信息
        Customer360DTO.StatisticsDTO statistics = new Customer360DTO.StatisticsDTO();
        statistics.setContactCount(contacts.size());
        statistics.setTotalOpportunityAmount(BigDecimal.ZERO);
        statistics.setTotalContractAmount(BigDecimal.ZERO);
        statistics.setTotalPaymentReceived(BigDecimal.ZERO);
        statistics.setOpportunityCount(0);
        statistics.setContractCount(0);
        statistics.setActivityCount(0);
        dto.setStatistics(statistics);

        return dto;
    }

    /**
     * 创建客户
     *
     * @param formData 表单数据
     * @return 客户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createCustomer(CustomerFormData formData) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(formData, customer);
        // 删除手动设置ID,由MyBatis-Plus自动生成

        // 生成客户编码
        customer.setCode(generateCustomerCode());

        // 设置默认状态
        if (customer.getStatus() == null) {
            customer.setStatus(1); // 正常
        }

        customerMapper.insert(customer);
        log.info("创建客户成功，ID: {}, 编码: {}", customer.getId(), customer.getCode());
        return customer.getId();
    }

    /**
     * 从线索创建客户
     *
     * @param leadId        线索ID
     * @param customerName  客户名称
     * @param customerType  客户类型
     * @param customerLevel 客户级别
     * @param ownerId       负责人ID
     * @return 客户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createFromLead(Long leadId, String customerName, String customerType, String customerLevel,
            Long ownerId) {
        Customer customer = new Customer();
        // 删除手动设置ID,由MyBatis-Plus自动生成
        customer.setName(customerName);
        customer.setCode(generateCustomerCode());
        customer.setLeadId(leadId);
        customer.setOwnerId(ownerId);
        customer.setType(customerType);
        customer.setLevel(customerLevel);
        customer.setStatus(1); // 正常

        customerMapper.insert(customer);
        log.info("从线索创建客户成功,线索ID: {}, 客户ID: {}, 级别: {}", leadId, customer.getId(), customerLevel);
        return customer.getId();
    }

    /**
     * 更新客户
     *
     * @param id       客户ID
     * @param formData 表单数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomer(Long id, CustomerFormData formData) {
        Customer existingCustomer = customerMapper.selectById(id);
        if (existingCustomer == null) {
            throw new BusinessException("客户不存在");
        }

        Customer customer = new Customer();
        BeanUtils.copyProperties(formData, customer);
        customer.setId(id);

        customerMapper.updateById(customer);
        log.info("更新客户成功，ID: {}", id);
    }

    /**
     * 删除客户
     *
     * @param id 客户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomer(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        // TODO: 检查是否有关联的商机、合同等

        // 删除关联的联系人
        LambdaQueryWrapper<Contact> contactWrapper = new LambdaQueryWrapper<>();
        contactWrapper.eq(Contact::getCustomerId, id);
        contactMapper.delete(contactWrapper);

        customerMapper.deleteById(id);
        log.info("删除客户成功，ID: {}", id);
    }

    /**
     * 释放客户到公海
     *
     * @param id     客户ID
     * @param reason 释放原因
     */
    @Transactional(rollbackFor = Exception.class)
    public void releaseToPool(Long id, String reason) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        if (customer.getStatus() != null && customer.getStatus() == 2) {
            throw new BusinessException("该客户已在公海中");
        }

        customer.setStatus(2); // 公海
        customer.setOwnerId(null); // 清空负责人
        customerMapper.updateById(customer);

        log.info("客户释放到公海成功，ID: {}, 原因: {}", id, reason);
    }

    /**
     * 从公海领取客户
     *
     * @param id      客户ID
     * @param ownerId 领取人ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void acquireFromPool(Long id, Long ownerId) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        if (customer.getStatus() == null || customer.getStatus() != 2) {
            throw new BusinessException("该客户不在公海中");
        }

        customer.setStatus(1); // 正常
        customer.setOwnerId(ownerId);
        customerMapper.updateById(customer);

        log.info("从公海领取客户成功，ID: {}, 领取人: {}", id, ownerId);
    }

    /**
     * 分配客户
     *
     * @param id      客户ID
     * @param ownerId 负责人ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignCustomer(Long id, Long ownerId) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        customer.setOwnerId(ownerId);
        if (customer.getStatus() != null && customer.getStatus() == 2) {
            customer.setStatus(1); // 从公海变为正常
        }
        customerMapper.updateById(customer);

        log.info("分配客户成功，ID: {}, 负责人: {}", id, ownerId);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<Customer> buildQueryWrapper(CustomerQueryParams params) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();

        // 客户名称模糊查询
        if (StringUtils.hasText(params.getName())) {
            wrapper.like(Customer::getName, params.getName());
        }

        // 行业
        if (StringUtils.hasText(params.getIndustry())) {
            wrapper.eq(Customer::getIndustry, params.getIndustry());
        }

        // 级别
        if (StringUtils.hasText(params.getLevel())) {
            wrapper.eq(Customer::getLevel, params.getLevel());
        }

        // 负责人
        if (params.getOwnerId() != null) {
            wrapper.eq(Customer::getOwnerId, params.getOwnerId());
        }

        // 状态
        if (params.getStatus() != null) {
            wrapper.eq(Customer::getStatus, params.getStatus());
        }

        // 公海客户查询
        if (params.getIsPublicPool() != null && params.getIsPublicPool()) {
            wrapper.eq(Customer::getStatus, 2);
        }

        // 按创建时间倒序
        wrapper.orderByDesc(Customer::getCreateTime);

        return wrapper;
    }

    /**
     * 生成客户编码
     */
    private String generateCustomerCode() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 简单实现：前缀 + 日期 + 随机数
        return CUSTOMER_CODE_PREFIX + dateStr + String.format("%04d", (int) (Math.random() * 10000));
    }

    /**
     * 转换为DTO
     */
    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        BeanUtils.copyProperties(customer, dto);

        // 设置状态名称
        dto.setStatusName(STATUS_MAP.get(customer.getStatus()));

        // TODO: 查询负责人姓名（需要关联用户表）

        return dto;
    }

    /**
     * 转换联系人为DTO
     */
    private ContactDTO convertContactToDTO(Contact contact) {
        ContactDTO dto = new ContactDTO();
        BeanUtils.copyProperties(contact, dto);

        // 设置性别名称
        if (contact.getGender() != null) {
            dto.setGenderName(contact.getGender() == 1 ? "男" : "女");
        }

        return dto;
    }
}
