package com.crm.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.business.dto.*;
import com.crm.business.entity.Lead;
import com.crm.business.mapper.LeadMapper;
import com.crm.common.exception.BusinessException;
import com.crm.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 线索服务
 * 对应数据库表 crm_lead
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadMapper leadMapper;
    private final CustomerService customerService;
    private final OpportunityService opportunityService;
    private final com.crm.system.service.UserService userService;

    /**
     * 线索状态映射
     */
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();

    static {
        STATUS_MAP.put(1, "新建");
        STATUS_MAP.put(2, "跟进中");
        STATUS_MAP.put(3, "已转化");
        STATUS_MAP.put(4, "已关闭");
    }

    /**
     * 分页查询线索列表
     */
    public PageResult<LeadDTO> getLeadList(LeadQueryParams params) {
        LambdaQueryWrapper<Lead> wrapper = buildQueryWrapper(params);

        // 分页查询
        Page<Lead> page = new Page<>(params.getPageNum(), params.getPageSize());
        IPage<Lead> leadPage = leadMapper.selectPage(page, wrapper);

        // 转换为DTO
        List<LeadDTO> dtoList = leadPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 批量填充负责人名称
        populateOwnerNames(dtoList);

        return new PageResult<>(
                dtoList,
                leadPage.getTotal(),
                leadPage.getCurrent(),
                leadPage.getSize());
    }

    /**
     * 根据ID获取线索详情
     */
    public LeadDTO getLeadById(Long id) {
        Lead lead = leadMapper.selectById(id);
        if (lead == null) {
            throw new BusinessException("线索不存在");
        }
        LeadDTO dto = convertToDTO(lead);

        // 填充负责人名称
        if (lead.getOwnerId() != null) {
            try {
                com.crm.system.dto.UserDTO user = userService.getUserById(lead.getOwnerId());
                dto.setOwnerName(StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getUsername());
            } catch (Exception e) {
                log.warn("获取负责人名称失败: {}", lead.getOwnerId());
            }
        }

        return dto;
    }

    /**
     * 创建线索
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createLead(LeadFormData formData) {
        Lead lead = new Lead();
        BeanUtils.copyProperties(formData, lead);
        // 删除手动设置ID,由MyBatis-Plus根据ASSIGN_ID配置自动生成

        // 设置默认状态
        if (lead.getStatus() == null) {
            lead.setStatus(1); // 新建
        }

        leadMapper.insert(lead);
        log.info("创建线索成功,ID: {}", lead.getId());
        return lead.getId();
    }

    /**
     * 更新线索
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateLead(Long id, LeadFormData formData) {
        Lead existingLead = leadMapper.selectById(id);
        if (existingLead == null) {
            throw new BusinessException("线索不存在");
        }

        Lead lead = new Lead();
        BeanUtils.copyProperties(formData, lead);
        lead.setId(id);

        leadMapper.updateById(lead);
        log.info("更新线索成功，ID: {}", id);
    }

    /**
     * 删除线索
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteLead(Long id) {
        Lead lead = leadMapper.selectById(id);
        if (lead == null) {
            throw new BusinessException("线索不存在");
        }

        // 已转化的线索不能删除
        if (lead.getStatus() != null && lead.getStatus() == 3) {
            throw new BusinessException("已转化的线索不能删除");
        }

        leadMapper.deleteById(id);
        log.info("删除线索成功，ID: {}", id);
    }

    /**
     * 批量删除线索
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteLeads(List<Long> ids) {
        // 检查是否有已转化的线索
        LambdaQueryWrapper<Lead> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Lead::getId, ids)
                .eq(Lead::getStatus, 3);
        long convertedCount = leadMapper.selectCount(wrapper);
        if (convertedCount > 0) {
            throw new BusinessException("选中的线索中包含已转化的线索，无法删除");
        }

        leadMapper.deleteBatchIds(ids);
        log.info("批量删除线索成功，数量: {}", ids.size());
    }

    /**
     * 转化线索为客户
     */
    @Transactional(rollbackFor = Exception.class)
    public Long convertLead(LeadConvertParams params) {
        Lead lead = leadMapper.selectById(params.getLeadId());
        if (lead == null) {
            throw new BusinessException("线索不存在");
        }

        if (lead.getStatus() != null && lead.getStatus() == 3) {
            throw new BusinessException("该线索已转化");
        }

        // 调用CustomerService创建客户记录（同时自动创建联系人）
        Long customerId = customerService.createFromLead(
                params.getLeadId(),
                params.getCustomerName(),
                params.getCustomerType(),
                params.getCustomerLevel(),
                params.getOwnerId() != null ? params.getOwnerId() : lead.getOwnerId(),
                params.getIndustry(),
                params.getScale(),
                params.getSource(),
                params.getCustomerPhone(),
                params.getContactName(),
                params.getContactPhone(),
                params.getContactEmail(),
                params.getContactPosition(),
                params.getContactGender());

        // 如果需要创建商机
        Long opportunityId = null;
        if (Boolean.TRUE.equals(params.getCreateOpportunity()) && params.getOpportunityName() != null) {
            OpportunityDTO opportunityDTO = new OpportunityDTO();
            opportunityDTO.setName(params.getOpportunityName());
            opportunityDTO.setCustomerId(customerId);
            opportunityDTO.setOwnerId(lead.getOwnerId());
            if (params.getOpportunityAmount() != null) {
                opportunityDTO.setAmount(new java.math.BigDecimal(params.getOpportunityAmount()));
            }
            if (params.getExpectedCloseDate() != null) {
                // 将时间戳转为LocalDate
                opportunityDTO.setExpectedDate(
                        new java.sql.Date(params.getExpectedCloseDate()).toLocalDate());
            }
            // 默认来源为线索转化
            opportunityDTO.setSource("LEAD");

            opportunityId = opportunityService.createOpportunity(opportunityDTO);
            log.info("线索转化时创建商机成功,商机ID: {}", opportunityId);
        }

        // 更新线索状态为已转化
        lead.setStatus(3);
        lead.setCustomerId(customerId);
        lead.setConvertTime(java.time.LocalDateTime.now());
        leadMapper.updateById(lead);

        log.info("线索转化成功,线索ID: {}, 客户ID: {}, 商机ID: {}", params.getLeadId(), customerId, opportunityId);
        return customerId;
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<Lead> buildQueryWrapper(LeadQueryParams params) {
        LambdaQueryWrapper<Lead> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（线索名称、公司名称、电话）
        if (StringUtils.hasText(params.getKeyword())) {
            wrapper.and(w -> w
                    .like(Lead::getName, params.getKeyword())
                    .or().like(Lead::getCompany, params.getKeyword())
                    .or().like(Lead::getPhone, params.getKeyword()));
        }

        // 来源
        if (StringUtils.hasText(params.getSource())) {
            wrapper.eq(Lead::getSource, params.getSource());
        }

        // 状态
        if (params.getStatus() != null) {
            wrapper.eq(Lead::getStatus, params.getStatus());
        }

        // 负责人
        if (params.getOwnerId() != null) {
            wrapper.eq(Lead::getOwnerId, params.getOwnerId());
        }

        // 行业
        if (StringUtils.hasText(params.getIndustry())) {
            wrapper.eq(Lead::getIndustry, params.getIndustry());
        }

        // 时间范围
        if (StringUtils.hasText(params.getStartTime())) {
            wrapper.ge(Lead::getCreateTime, params.getStartTime());
        }
        if (StringUtils.hasText(params.getEndTime())) {
            wrapper.le(Lead::getCreateTime, params.getEndTime());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(Lead::getCreateTime);

        return wrapper;
    }

    /**
     * 批量导入线索
     *
     * @param dataList 线索数据列表
     * @return 导入成功数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchImport(List<Lead> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            throw new BusinessException("导入数据不能为空");
        }

        int successCount = 0;
        for (Lead lead : dataList) {
            try {
                // 删除手动设置ID,由MyBatis-Plus自动生成

                // 设置默认值
                if (lead.getStatus() == null) {
                    lead.setStatus(1); // 新建
                }

                leadMapper.insert(lead);
                successCount++;
            } catch (Exception e) {
                log.error("导入线索失败: {}", lead.getName(), e);
            }
        }

        log.info("批量导入线索完成，成功: {}, 总数: {}", successCount, dataList.size());
        return successCount;
    }

    /**
     * 批量导出线索
     *
     * @param params 查询参数
     * @return 线索列表
     */
    public List<Lead> batchExport(LeadQueryParams params) {
        LambdaQueryWrapper<Lead> wrapper = buildQueryWrapper(params);
        // 导出时不分页，但限制最大数量
        wrapper.last("LIMIT 10000");
        return leadMapper.selectList(wrapper);
    }

    /**
     * 获取线索统计数据
     */
    public Map<String, Object> getLeadStats() {
        Map<String, Object> stats = new HashMap<>();

        // 统计总数
        Long total = leadMapper.selectCount(null);
        stats.put("total", total);

        // 统计各状态数量
        // 1: 新建, 2: 跟进中, 3: 已转化, 4: 已失效

        LambdaQueryWrapper<Lead> newWrapper = new LambdaQueryWrapper<>();
        newWrapper.eq(Lead::getStatus, 1);
        stats.put("new", leadMapper.selectCount(newWrapper));

        LambdaQueryWrapper<Lead> followingWrapper = new LambdaQueryWrapper<>();
        followingWrapper.eq(Lead::getStatus, 2);
        stats.put("following", leadMapper.selectCount(followingWrapper));

        LambdaQueryWrapper<Lead> convertedWrapper = new LambdaQueryWrapper<>();
        convertedWrapper.eq(Lead::getStatus, 3);
        stats.put("converted", leadMapper.selectCount(convertedWrapper));

        LambdaQueryWrapper<Lead> invalidWrapper = new LambdaQueryWrapper<>();
        invalidWrapper.eq(Lead::getStatus, 4);
        stats.put("invalid", leadMapper.selectCount(invalidWrapper));

        return stats;
    }

    /**
     * 转换为DTO
     */
    private LeadDTO convertToDTO(Lead lead) {
        LeadDTO dto = new LeadDTO();
        BeanUtils.copyProperties(lead, dto);

        // 设置状态名称
        dto.setStatusName(STATUS_MAP.get(lead.getStatus()));

        // TODO: 查询负责人姓名（需要关联用户表）

        return dto;
    }

    /**
     * 批量填充负责人名称
     */
    private void populateOwnerNames(List<LeadDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return;
        }

        // 收集所有负责人ID
        List<Long> ownerIds = dtoList.stream()
                .map(LeadDTO::getOwnerId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        if (ownerIds.isEmpty()) {
            return;
        }

        // 批量查询用户
        try {
            List<com.crm.system.dto.UserDTO> users = userService.getUsersByIds(ownerIds);
            Map<Long, String> userMap = users.stream()
                    .collect(Collectors.toMap(
                            com.crm.system.dto.UserDTO::getId,
                            u -> StringUtils.hasText(u.getNickname()) ? u.getNickname() : u.getUsername(),
                            (existing, replacement) -> existing));

            // 填充名称
            dtoList.forEach(dto -> {
                if (dto.getOwnerId() != null) {
                    dto.setOwnerName(userMap.get(dto.getOwnerId()));
                }
            });
        } catch (Exception e) {
            log.error("批量查询负责人失败", e);
        }
    }
}
