package com.crm.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.business.dto.PaymentPlanDTO;
import com.crm.business.dto.PaymentRecordDTO;
import com.crm.business.entity.Contract;
import com.crm.business.entity.Customer;
import com.crm.business.entity.PaymentPlan;
import com.crm.business.entity.PaymentRecord;
import com.crm.business.mapper.ContractMapper;
import com.crm.business.mapper.CustomerMapper;
import com.crm.business.mapper.PaymentPlanMapper;
import com.crm.business.mapper.PaymentRecordMapper;
import com.crm.common.exception.BusinessException;
import com.crm.common.utils.IdGenerator;
import com.crm.system.entity.User;
import com.crm.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 回款服务
 * 提供回款计划和回款记录的管理功能
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentPlanMapper paymentPlanMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final ContractMapper contractMapper;
    private final CustomerMapper customerMapper;
    private final UserMapper userMapper;
    private final IdGenerator idGenerator;

    /**
     * 回款计划状态常量
     */
    public static final Integer STATUS_PENDING = 1;      // 待回款
    public static final Integer STATUS_PARTIAL = 2;      // 部分回款
    public static final Integer STATUS_COMPLETED = 3;    // 已回款
    public static final Integer STATUS_OVERDUE = 4;      // 逾期

    /**
     * 状态名称映射
     */
    private static final Map<Integer, String> STATUS_NAME_MAP = new HashMap<>();
    static {
        STATUS_NAME_MAP.put(STATUS_PENDING, "待回款");
        STATUS_NAME_MAP.put(STATUS_PARTIAL, "部分回款");
        STATUS_NAME_MAP.put(STATUS_COMPLETED, "已回款");
        STATUS_NAME_MAP.put(STATUS_OVERDUE, "逾期");
    }

    /**
     * 分页查询回款计划列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param contractId 合同ID
     * @param status 状态
     * @return 分页结果
     */
    public IPage<PaymentPlanDTO> getPaymentPlanPage(Integer pageNum, Integer pageSize,
                                                     Long contractId, Integer status) {
        Page<PaymentPlan> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PaymentPlan> wrapper = new LambdaQueryWrapper<>();

        if (contractId != null) {
            wrapper.eq(PaymentPlan::getContractId, contractId);
        }
        if (status != null) {
            wrapper.eq(PaymentPlan::getStatus, status);
        }

        wrapper.orderByAsc(PaymentPlan::getPlanDate);

        IPage<PaymentPlan> planPage = paymentPlanMapper.selectPage(page, wrapper);

        return planPage.convert(this::convertPlanToDTO);
    }

    /**
     * 根据合同ID获取回款计划列表
     *
     * @param contractId 合同ID
     * @return 回款计划列表
     */
    public List<PaymentPlanDTO> getPaymentPlansByContractId(Long contractId) {
        LambdaQueryWrapper<PaymentPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentPlan::getContractId, contractId)
               .orderByAsc(PaymentPlan::getPeriod);
        List<PaymentPlan> plans = paymentPlanMapper.selectList(wrapper);
        return plans.stream()
                .map(this::convertPlanToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取回款计划详情
     *
     * @param id 回款计划ID
     * @return 回款计划DTO
     */
    public PaymentPlanDTO getPaymentPlanById(Long id) {
        PaymentPlan plan = paymentPlanMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException("回款计划不存在");
        }
        PaymentPlanDTO dto = convertPlanToDTO(plan);
        // 加载回款记录
        dto.setRecords(getPaymentRecordsByPlanId(id));
        return dto;
    }

    /**
     * 创建回款计划
     *
     * @param formData 表单数据
     * @return 回款计划ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createPaymentPlan(PaymentPlanDTO formData) {
        // 验证合同是否存在
        Contract contract = contractMapper.selectById(formData.getContractId());
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        // 检查期数是否重复
        LambdaQueryWrapper<PaymentPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentPlan::getContractId, formData.getContractId())
               .eq(PaymentPlan::getPeriod, formData.getPeriod());
        if (paymentPlanMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该期数的回款计划已存在");
        }

        PaymentPlan plan = new PaymentPlan();
        BeanUtils.copyProperties(formData, plan);
        plan.setId(idGenerator.nextId());

        // 设置默认值
        if (plan.getActualAmount() == null) {
            plan.setActualAmount(BigDecimal.ZERO);
        }
        if (plan.getStatus() == null) {
            plan.setStatus(STATUS_PENDING);
        }

        paymentPlanMapper.insert(plan);
        log.info("创建回款计划成功，ID: {}, 合同ID: {}, 期数: {}", plan.getId(), plan.getContractId(), plan.getPeriod());
        return plan.getId();
    }

    /**
     * 批量创建回款计划
     *
     * @param contractId 合同ID
     * @param plans 回款计划列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchCreatePaymentPlans(Long contractId, List<PaymentPlanDTO> plans) {
        // 验证合同是否存在
        Contract contract = contractMapper.selectById(contractId);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        // 删除原有计划
        LambdaQueryWrapper<PaymentPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentPlan::getContractId, contractId);
        paymentPlanMapper.delete(wrapper);

        // 创建新计划
        for (PaymentPlanDTO planDTO : plans) {
            planDTO.setContractId(contractId);
            createPaymentPlan(planDTO);
        }

        log.info("批量创建回款计划成功，合同ID: {}, 数量: {}", contractId, plans.size());
    }

    /**
     * 更新回款计划
     *
     * @param id 回款计划ID
     * @param formData 表单数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePaymentPlan(Long id, PaymentPlanDTO formData) {
        PaymentPlan existingPlan = paymentPlanMapper.selectById(id);
        if (existingPlan == null) {
            throw new BusinessException("回款计划不存在");
        }

        // 已完成的计划不能修改
        if (STATUS_COMPLETED.equals(existingPlan.getStatus())) {
            throw new BusinessException("已完成的回款计划不能修改");
        }

        PaymentPlan plan = new PaymentPlan();
        BeanUtils.copyProperties(formData, plan);
        plan.setId(id);

        paymentPlanMapper.updateById(plan);
        log.info("更新回款计划成功，ID: {}", id);
    }

    /**
     * 删除回款计划
     *
     * @param id 回款计划ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletePaymentPlan(Long id) {
        PaymentPlan plan = paymentPlanMapper.selectById(id);
        if (plan == null) {
            throw new BusinessException("回款计划不存在");
        }

        // 检查是否有回款记录
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getPlanId, id);
        if (paymentRecordMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该回款计划已有回款记录，不能删除");
        }

        paymentPlanMapper.deleteById(id);
        log.info("删除回款计划成功，ID: {}", id);
    }

    /**
     * 获取回款计划的回款记录列表
     *
     * @param planId 回款计划ID
     * @return 回款记录列表
     */
    public List<PaymentRecordDTO> getPaymentRecordsByPlanId(Long planId) {
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getPlanId, planId)
               .orderByDesc(PaymentRecord::getPaymentDate);
        List<PaymentRecord> records = paymentRecordMapper.selectList(wrapper);
        return records.stream()
                .map(this::convertRecordToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 创建回款记录
     *
     * @param formData 表单数据
     * @return 回款记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createPaymentRecord(PaymentRecordDTO formData) {
        // 验证回款计划是否存在
        PaymentPlan plan = paymentPlanMapper.selectById(formData.getPlanId());
        if (plan == null) {
            throw new BusinessException("回款计划不存在");
        }

        // 已完成的计划不能再添加回款记录
        if (STATUS_COMPLETED.equals(plan.getStatus())) {
            throw new BusinessException("该回款计划已完成，不能再添加回款记录");
        }

        PaymentRecord record = new PaymentRecord();
        BeanUtils.copyProperties(formData, record);
        record.setId(idGenerator.nextId());

        paymentRecordMapper.insert(record);

        // 更新回款计划的实际回款金额和状态
        updatePlanAfterPayment(plan.getId());

        log.info("创建回款记录成功，ID: {}, 计划ID: {}, 金额: {}", record.getId(), record.getPlanId(), record.getAmount());
        return record.getId();
    }

    /**
     * 删除回款记录
     *
     * @param id 回款记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletePaymentRecord(Long id) {
        PaymentRecord record = paymentRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("回款记录不存在");
        }

        Long planId = record.getPlanId();
        paymentRecordMapper.deleteById(id);

        // 更新回款计划的实际回款金额和状态
        updatePlanAfterPayment(planId);

        log.info("删除回款记录成功，ID: {}", id);
    }

    /**
     * 更新回款计划的实际回款金额和状态
     *
     * @param planId 回款计划ID
     */
    private void updatePlanAfterPayment(Long planId) {
        PaymentPlan plan = paymentPlanMapper.selectById(planId);
        if (plan == null) {
            return;
        }

        // 计算实际回款总额
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getPlanId, planId);
        List<PaymentRecord> records = paymentRecordMapper.selectList(wrapper);

        BigDecimal actualAmount = records.stream()
                .map(PaymentRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 更新计划
        PaymentPlan updatePlan = new PaymentPlan();
        updatePlan.setId(planId);
        updatePlan.setActualAmount(actualAmount);

        // 确定状态
        if (actualAmount.compareTo(BigDecimal.ZERO) == 0) {
            // 无回款
            if (plan.getPlanDate().isBefore(LocalDate.now())) {
                updatePlan.setStatus(STATUS_OVERDUE);
            } else {
                updatePlan.setStatus(STATUS_PENDING);
            }
        } else if (actualAmount.compareTo(plan.getPlanAmount()) >= 0) {
            // 已完成
            updatePlan.setStatus(STATUS_COMPLETED);
            // 设置实际回款日期为最后一笔回款日期
            if (!records.isEmpty()) {
                LocalDate lastPaymentDate = records.stream()
                        .map(PaymentRecord::getPaymentDate)
                        .max(LocalDate::compareTo)
                        .orElse(LocalDate.now());
                updatePlan.setActualDate(lastPaymentDate);
            }
        } else {
            // 部分回款
            updatePlan.setStatus(STATUS_PARTIAL);
        }

        paymentPlanMapper.updateById(updatePlan);
    }

    /**
     * 检查并更新逾期状态
     * 此方法应由定时任务调用
     */
    @Transactional(rollbackFor = Exception.class)
    public void checkAndUpdateOverdueStatus() {
        LambdaQueryWrapper<PaymentPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentPlan::getStatus, STATUS_PENDING)
               .lt(PaymentPlan::getPlanDate, LocalDate.now());

        List<PaymentPlan> overduePlans = paymentPlanMapper.selectList(wrapper);

        for (PaymentPlan plan : overduePlans) {
            PaymentPlan updatePlan = new PaymentPlan();
            updatePlan.setId(plan.getId());
            updatePlan.setStatus(STATUS_OVERDUE);
            paymentPlanMapper.updateById(updatePlan);
        }

        if (!overduePlans.isEmpty()) {
            log.info("更新逾期回款计划数量: {}", overduePlans.size());
        }
    }

    /**
     * 将回款计划实体转换为DTO
     *
     * @param plan 回款计划实体
     * @return 回款计划DTO
     */
    private PaymentPlanDTO convertPlanToDTO(PaymentPlan plan) {
        PaymentPlanDTO dto = new PaymentPlanDTO();
        BeanUtils.copyProperties(plan, dto);

        // 设置状态名称
        dto.setStatusName(STATUS_NAME_MAP.getOrDefault(plan.getStatus(), "未知"));

        // 加载合同信息
        if (plan.getContractId() != null) {
            Contract contract = contractMapper.selectById(plan.getContractId());
            if (contract != null) {
                dto.setContractNo(contract.getContractNo());
                dto.setContractName(contract.getName());
                dto.setCustomerId(contract.getCustomerId());

                // 加载客户名称
                if (contract.getCustomerId() != null) {
                    Customer customer = customerMapper.selectById(contract.getCustomerId());
                    if (customer != null) {
                        dto.setCustomerName(customer.getName());
                    }
                }
            }
        }

        return dto;
    }

    /**
     * 将回款记录实体转换为DTO
     *
     * @param record 回款记录实体
     * @return 回款记录DTO
     */
    private PaymentRecordDTO convertRecordToDTO(PaymentRecord record) {
        PaymentRecordDTO dto = new PaymentRecordDTO();
        BeanUtils.copyProperties(record, dto);

        // 加载创建人名称
        if (record.getCreateBy() != null) {
            User user = userMapper.selectById(record.getCreateBy());
            if (user != null) {
                dto.setCreateByName(user.getNickname());
            }
        }

        return dto;
    }

    /**
     * 获取回款统计数据
     *
     * @param contractId 合同ID（可选）
     * @return 统计数据
     */
    public Map<String, Object> getPaymentStatistics(Long contractId) {
        Map<String, Object> statistics = new HashMap<>();

        LambdaQueryWrapper<PaymentPlan> wrapper = new LambdaQueryWrapper<>();
        if (contractId != null) {
            wrapper.eq(PaymentPlan::getContractId, contractId);
        }

        List<PaymentPlan> plans = paymentPlanMapper.selectList(wrapper);

        // 计划总金额
        BigDecimal totalPlanAmount = plans.stream()
                .map(PaymentPlan::getPlanAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("totalPlanAmount", totalPlanAmount);

        // 实际回款总额
        BigDecimal totalActualAmount = plans.stream()
                .map(PaymentPlan::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("totalActualAmount", totalActualAmount);

        // 未回款金额
        BigDecimal unpaidAmount = totalPlanAmount.subtract(totalActualAmount);
        statistics.put("unpaidAmount", unpaidAmount);

        // 各状态数量
        Map<String, Long> statusCount = new HashMap<>();
        for (Map.Entry<Integer, String> entry : STATUS_NAME_MAP.entrySet()) {
            long count = plans.stream()
                    .filter(p -> entry.getKey().equals(p.getStatus()))
                    .count();
            statusCount.put(entry.getValue(), count);
        }
        statistics.put("statusCount", statusCount);

        return statistics;
    }
}
