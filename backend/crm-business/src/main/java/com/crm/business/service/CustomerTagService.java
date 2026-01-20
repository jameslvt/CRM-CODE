package com.crm.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.business.dto.CustomerTagDTO;
import com.crm.business.entity.CustomerTag;
import com.crm.business.entity.CustomerTagRelation;
import com.crm.business.mapper.CustomerTagMapper;
import com.crm.business.mapper.CustomerTagRelationMapper;
import com.crm.common.exception.BusinessException;
import com.crm.common.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户标签服务
 * 提供标签的CRUD和客户标签关联管理
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerTagService {

    private final CustomerTagMapper customerTagMapper;
    private final CustomerTagRelationMapper customerTagRelationMapper;
    private final IdGenerator idGenerator;

    /**
     * 获取所有标签列表
     *
     * @return 标签列表
     */
    public List<CustomerTag> getAllTags() {
        LambdaQueryWrapper<CustomerTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CustomerTag::getSort)
               .orderByDesc(CustomerTag::getCreateTime);
        return customerTagMapper.selectList(wrapper);
    }

    /**
     * 根据ID获取标签
     *
     * @param id 标签ID
     * @return 标签
     */
    public CustomerTag getTagById(Long id) {
        CustomerTag tag = customerTagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        return tag;
    }

    /**
     * 创建标签
     *
     * @param formData 表单数据
     * @return 标签ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createTag(CustomerTagDTO formData) {
        // 检查标签名称是否重复
        LambdaQueryWrapper<CustomerTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerTag::getName, formData.getName());
        if (customerTagMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("标签名称已存在");
        }

        CustomerTag tag = new CustomerTag();
        BeanUtils.copyProperties(formData, tag);
        tag.setId(idGenerator.nextId());

        // 设置默认排序
        if (tag.getSort() == null) {
            tag.setSort(0);
        }

        // 设置默认颜色
        if (!StringUtils.hasText(tag.getColor())) {
            tag.setColor("#1890ff");
        }

        customerTagMapper.insert(tag);
        log.info("创建客户标签成功，ID: {}, 名称: {}", tag.getId(), tag.getName());
        return tag.getId();
    }

    /**
     * 更新标签
     *
     * @param id 标签ID
     * @param formData 表单数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTag(Long id, CustomerTagDTO formData) {
        CustomerTag existingTag = customerTagMapper.selectById(id);
        if (existingTag == null) {
            throw new BusinessException("标签不存在");
        }

        // 检查标签名称是否重复（排除自身）
        LambdaQueryWrapper<CustomerTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerTag::getName, formData.getName())
               .ne(CustomerTag::getId, id);
        if (customerTagMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("标签名称已存在");
        }

        CustomerTag tag = new CustomerTag();
        BeanUtils.copyProperties(formData, tag);
        tag.setId(id);

        customerTagMapper.updateById(tag);
        log.info("更新客户标签成功，ID: {}", id);
    }

    /**
     * 删除标签
     *
     * @param id 标签ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long id) {
        CustomerTag tag = customerTagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }

        // 删除标签关联关系
        LambdaQueryWrapper<CustomerTagRelation> relationWrapper = new LambdaQueryWrapper<>();
        relationWrapper.eq(CustomerTagRelation::getTagId, id);
        customerTagRelationMapper.delete(relationWrapper);

        // 删除标签
        customerTagMapper.deleteById(id);
        log.info("删除客户标签成功，ID: {}", id);
    }

    /**
     * 获取客户的标签列表
     *
     * @param customerId 客户ID
     * @return 标签列表
     */
    public List<CustomerTag> getTagsByCustomerId(Long customerId) {
        // 查询客户关联的标签ID
        LambdaQueryWrapper<CustomerTagRelation> relationWrapper = new LambdaQueryWrapper<>();
        relationWrapper.eq(CustomerTagRelation::getCustomerId, customerId);
        List<CustomerTagRelation> relations = customerTagRelationMapper.selectList(relationWrapper);

        if (relations.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询标签详情
        List<Long> tagIds = relations.stream()
            .map(CustomerTagRelation::getTagId)
            .collect(Collectors.toList());

        LambdaQueryWrapper<CustomerTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.in(CustomerTag::getId, tagIds)
                  .orderByAsc(CustomerTag::getSort);
        return customerTagMapper.selectList(tagWrapper);
    }

    /**
     * 设置客户的标签
     *
     * @param customerId 客户ID
     * @param tagIds 标签ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void setCustomerTags(Long customerId, List<Long> tagIds) {
        // 删除原有关联
        LambdaQueryWrapper<CustomerTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerTagRelation::getCustomerId, customerId);
        customerTagRelationMapper.delete(wrapper);

        // 添加新关联
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                CustomerTagRelation relation = new CustomerTagRelation();
                relation.setId(idGenerator.nextId());
                relation.setCustomerId(customerId);
                relation.setTagId(tagId);
                customerTagRelationMapper.insert(relation);
            }
        }

        log.info("设置客户标签成功，客户ID: {}, 标签数量: {}", customerId, tagIds != null ? tagIds.size() : 0);
    }

    /**
     * 为客户添加标签
     *
     * @param customerId 客户ID
     * @param tagId 标签ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void addTagToCustomer(Long customerId, Long tagId) {
        // 检查是否已存在关联
        LambdaQueryWrapper<CustomerTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerTagRelation::getCustomerId, customerId)
               .eq(CustomerTagRelation::getTagId, tagId);
        if (customerTagRelationMapper.selectCount(wrapper) > 0) {
            return; // 已存在，不重复添加
        }

        CustomerTagRelation relation = new CustomerTagRelation();
        relation.setId(idGenerator.nextId());
        relation.setCustomerId(customerId);
        relation.setTagId(tagId);
        customerTagRelationMapper.insert(relation);

        log.info("为客户添加标签成功，客户ID: {}, 标签ID: {}", customerId, tagId);
    }

    /**
     * 移除客户的标签
     *
     * @param customerId 客户ID
     * @param tagId 标签ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeTagFromCustomer(Long customerId, Long tagId) {
        LambdaQueryWrapper<CustomerTagRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerTagRelation::getCustomerId, customerId)
               .eq(CustomerTagRelation::getTagId, tagId);
        customerTagRelationMapper.delete(wrapper);

        log.info("移除客户标签成功，客户ID: {}, 标签ID: {}", customerId, tagId);
    }
}
