package com.crm.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.business.dto.ActivityDTO;
import com.crm.business.entity.Activity;
import com.crm.business.entity.Customer;
import com.crm.business.entity.Lead;
import com.crm.business.entity.Opportunity;
import com.crm.business.mapper.ActivityMapper;
import com.crm.business.mapper.CustomerMapper;
import com.crm.business.mapper.LeadMapper;
import com.crm.business.mapper.OpportunityMapper;
import com.crm.common.exception.BusinessException;
import com.crm.common.utils.IdGenerator;
import com.crm.system.entity.User;
import com.crm.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 跟进记录服务
 * 提供跟进记录的CRUD和按对象查询功能
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityMapper activityMapper;
    private final LeadMapper leadMapper;
    private final CustomerMapper customerMapper;
    private final OpportunityMapper opportunityMapper;
    private final UserMapper userMapper;
    private final IdGenerator idGenerator;

    /**
     * 跟进类型常量
     */
    public static final String TYPE_PHONE = "phone";        // 电话
    public static final String TYPE_VISIT = "visit";        // 拜访
    public static final String TYPE_EMAIL = "email";        // 邮件
    public static final String TYPE_MEETING = "meeting";    // 会议
    public static final String TYPE_OTHER = "other";        // 其他

    /**
     * 关联对象类型常量
     */
    public static final String TARGET_TYPE_LEAD = "lead";
    public static final String TARGET_TYPE_CUSTOMER = "customer";
    public static final String TARGET_TYPE_OPPORTUNITY = "opportunity";

    /**
     * 跟进类型名称映射
     */
    private static final Map<String, String> TYPE_NAME_MAP = new HashMap<>();
    static {
        TYPE_NAME_MAP.put(TYPE_PHONE, "电话");
        TYPE_NAME_MAP.put(TYPE_VISIT, "拜访");
        TYPE_NAME_MAP.put(TYPE_EMAIL, "邮件");
        TYPE_NAME_MAP.put(TYPE_MEETING, "会议");
        TYPE_NAME_MAP.put(TYPE_OTHER, "其他");
    }

    /**
     * 关联对象类型名称映射
     */
    private static final Map<String, String> TARGET_TYPE_NAME_MAP = new HashMap<>();
    static {
        TARGET_TYPE_NAME_MAP.put(TARGET_TYPE_LEAD, "线索");
        TARGET_TYPE_NAME_MAP.put(TARGET_TYPE_CUSTOMER, "客户");
        TARGET_TYPE_NAME_MAP.put(TARGET_TYPE_OPPORTUNITY, "商机");
    }

    /**
     * 分页查询跟进记录列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param targetType 关联对象类型
     * @param targetId 关联对象ID
     * @param type 跟进类型
     * @param createBy 创建人ID
     * @return 分页结果
     */
    public IPage<ActivityDTO> getActivityPage(Integer pageNum, Integer pageSize,
                                               String targetType, Long targetId,
                                               String type, Long createBy) {
        Page<Activity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(targetType)) {
            wrapper.eq(Activity::getTargetType, targetType);
        }
        if (targetId != null) {
            wrapper.eq(Activity::getTargetId, targetId);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(Activity::getType, type);
        }
        if (createBy != null) {
            wrapper.eq(Activity::getCreateBy, createBy);
        }

        wrapper.orderByDesc(Activity::getCreateTime);

        IPage<Activity> activityPage = activityMapper.selectPage(page, wrapper);

        return activityPage.convert(this::convertToDTO);
    }

    /**
     * 根据关联对象获取跟进记录列表（时间线）
     *
     * @param targetType 关联对象类型
     * @param targetId 关联对象ID
     * @return 跟进记录列表
     */
    public List<ActivityDTO> getActivitiesByTarget(String targetType, Long targetId) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getTargetType, targetType)
               .eq(Activity::getTargetId, targetId)
               .orderByDesc(Activity::getCreateTime);
        List<Activity> activities = activityMapper.selectList(wrapper);
        return activities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取跟进记录详情
     *
     * @param id 跟进记录ID
     * @return 跟进记录DTO
     */
    public ActivityDTO getActivityById(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("跟进记录不存在");
        }
        return convertToDTO(activity);
    }

    /**
     * 创建跟进记录
     *
     * @param formData 表单数据
     * @return 跟进记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createActivity(ActivityDTO formData) {
        // 验证关联对象是否存在
        validateTarget(formData.getTargetType(), formData.getTargetId());

        // 验证跟进类型
        if (!TYPE_NAME_MAP.containsKey(formData.getType())) {
            throw new BusinessException("无效的跟进类型");
        }

        Activity activity = new Activity();
        BeanUtils.copyProperties(formData, activity);
        activity.setId(idGenerator.nextId());

        activityMapper.insert(activity);
        log.info("创建跟进记录成功，ID: {}, 类型: {}, 关联: {}:{}",
                activity.getId(), activity.getType(), activity.getTargetType(), activity.getTargetId());
        return activity.getId();
    }

    /**
     * 更新跟进记录
     *
     * @param id 跟进记录ID
     * @param formData 表单数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateActivity(Long id, ActivityDTO formData) {
        Activity existingActivity = activityMapper.selectById(id);
        if (existingActivity == null) {
            throw new BusinessException("跟进记录不存在");
        }

        // 验证跟进类型
        if (StringUtils.hasText(formData.getType()) && !TYPE_NAME_MAP.containsKey(formData.getType())) {
            throw new BusinessException("无效的跟进类型");
        }

        Activity activity = new Activity();
        activity.setId(id);
        activity.setType(formData.getType());
        activity.setContent(formData.getContent());
        activity.setNextTime(formData.getNextTime());

        activityMapper.updateById(activity);
        log.info("更新跟进记录成功，ID: {}", id);
    }

    /**
     * 删除跟进记录
     *
     * @param id 跟进记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(Long id) {
        Activity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException("跟进记录不存在");
        }

        activityMapper.deleteById(id);
        log.info("删除跟进记录成功，ID: {}", id);
    }

    /**
     * 获取待跟进列表（下次跟进时间在指定范围内）
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 跟进记录列表
     */
    public List<ActivityDTO> getPendingActivities(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getCreateBy, userId)
               .isNotNull(Activity::getNextTime)
               .ge(Activity::getNextTime, startTime)
               .le(Activity::getNextTime, endTime)
               .orderByAsc(Activity::getNextTime);
        List<Activity> activities = activityMapper.selectList(wrapper);
        return activities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取今日待跟进列表
     *
     * @param userId 用户ID
     * @return 跟进记录列表
     */
    public List<ActivityDTO> getTodayPendingActivities(Long userId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        return getPendingActivities(userId, startOfDay, endOfDay);
    }

    /**
     * 验证关联对象是否存在
     *
     * @param targetType 关联对象类型
     * @param targetId 关联对象ID
     */
    private void validateTarget(String targetType, Long targetId) {
        switch (targetType) {
            case TARGET_TYPE_LEAD:
                Lead lead = leadMapper.selectById(targetId);
                if (lead == null) {
                    throw new BusinessException("线索不存在");
                }
                break;
            case TARGET_TYPE_CUSTOMER:
                Customer customer = customerMapper.selectById(targetId);
                if (customer == null) {
                    throw new BusinessException("客户不存在");
                }
                break;
            case TARGET_TYPE_OPPORTUNITY:
                Opportunity opportunity = opportunityMapper.selectById(targetId);
                if (opportunity == null) {
                    throw new BusinessException("商机不存在");
                }
                break;
            default:
                throw new BusinessException("无效的关联对象类型");
        }
    }

    /**
     * 获取关联对象名称
     *
     * @param targetType 关联对象类型
     * @param targetId 关联对象ID
     * @return 关联对象名称
     */
    private String getTargetName(String targetType, Long targetId) {
        switch (targetType) {
            case TARGET_TYPE_LEAD:
                Lead lead = leadMapper.selectById(targetId);
                return lead != null ? lead.getName() : null;
            case TARGET_TYPE_CUSTOMER:
                Customer customer = customerMapper.selectById(targetId);
                return customer != null ? customer.getName() : null;
            case TARGET_TYPE_OPPORTUNITY:
                Opportunity opportunity = opportunityMapper.selectById(targetId);
                return opportunity != null ? opportunity.getName() : null;
            default:
                return null;
        }
    }

    /**
     * 将实体转换为DTO
     *
     * @param activity 跟进记录实体
     * @return 跟进记录DTO
     */
    private ActivityDTO convertToDTO(Activity activity) {
        ActivityDTO dto = new ActivityDTO();
        BeanUtils.copyProperties(activity, dto);

        // 设置类型名称
        dto.setTypeName(TYPE_NAME_MAP.getOrDefault(activity.getType(), activity.getType()));

        // 设置关联对象类型名称
        dto.setTargetTypeName(TARGET_TYPE_NAME_MAP.getOrDefault(activity.getTargetType(), activity.getTargetType()));

        // 设置关联对象名称
        dto.setTargetName(getTargetName(activity.getTargetType(), activity.getTargetId()));

        // 加载创建人名称
        if (activity.getCreateBy() != null) {
            User user = userMapper.selectById(activity.getCreateBy());
            if (user != null) {
                dto.setCreateByName(user.getNickname());
            }
        }

        return dto;
    }

    /**
     * 获取跟进统计数据
     *
     * @param targetType 关联对象类型
     * @param targetId 关联对象ID
     * @return 统计数据
     */
    public Map<String, Object> getActivityStatistics(String targetType, Long targetId) {
        Map<String, Object> statistics = new HashMap<>();

        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(targetType)) {
            wrapper.eq(Activity::getTargetType, targetType);
        }
        if (targetId != null) {
            wrapper.eq(Activity::getTargetId, targetId);
        }

        // 总跟进数
        Long totalCount = activityMapper.selectCount(wrapper);
        statistics.put("totalCount", totalCount);

        // 各类型跟进数
        Map<String, Long> typeCount = new HashMap<>();
        for (String type : TYPE_NAME_MAP.keySet()) {
            LambdaQueryWrapper<Activity> typeWrapper = new LambdaQueryWrapper<>();
            typeWrapper.eq(Activity::getType, type);
            if (StringUtils.hasText(targetType)) {
                typeWrapper.eq(Activity::getTargetType, targetType);
            }
            if (targetId != null) {
                typeWrapper.eq(Activity::getTargetId, targetId);
            }
            typeCount.put(TYPE_NAME_MAP.get(type), activityMapper.selectCount(typeWrapper));
        }
        statistics.put("typeCount", typeCount);

        return statistics;
    }
}
