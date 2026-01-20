package com.crm.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.business.dto.ContractDTO;
import com.crm.business.dto.ContractQueryParams;
import com.crm.business.entity.Contract;
import com.crm.business.entity.Customer;
import com.crm.business.entity.Opportunity;
import com.crm.business.event.ContractSignedEvent;
import com.crm.business.mapper.ContractMapper;
import com.crm.business.mapper.CustomerMapper;
import com.crm.business.mapper.OpportunityMapper;
import com.crm.common.exception.BusinessException;
import com.crm.common.utils.IdGenerator;
import com.crm.system.entity.User;
import com.crm.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 合同服务
 * 提供合同的CRUD、状态流转等功能
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractMapper contractMapper;
    private final CustomerMapper customerMapper;
    private final OpportunityMapper opportunityMapper;
    private final UserMapper userMapper;
    private final IdGenerator idGenerator;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 合同状态常量
     */
    public static final Integer STATUS_DRAFT = 1;        // 草稿
    public static final Integer STATUS_APPROVING = 2;    // 审批中
    public static final Integer STATUS_EXECUTING = 3;    // 执行中
    public static final Integer STATUS_COMPLETED = 4;    // 已完成
    public static final Integer STATUS_TERMINATED = 5;   // 已终止

    /**
     * 状态名称映射
     */
    private static final Map<Integer, String> STATUS_NAME_MAP = new HashMap<>();
    static {
        STATUS_NAME_MAP.put(STATUS_DRAFT, "草稿");
        STATUS_NAME_MAP.put(STATUS_APPROVING, "审批中");
        STATUS_NAME_MAP.put(STATUS_EXECUTING, "执行中");
        STATUS_NAME_MAP.put(STATUS_COMPLETED, "已完成");
        STATUS_NAME_MAP.put(STATUS_TERMINATED, "已终止");
    }

    /**
     * 分页查询合同列表
     *
     * @param params 查询参数
     * @return 分页结果
     */
    public IPage<ContractDTO> getContractPage(ContractQueryParams params) {
        Page<Contract> page = new Page<>(params.getPageNum(), params.getPageSize());
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (StringUtils.hasText(params.getContractNo())) {
            wrapper.like(Contract::getContractNo, params.getContractNo());
        }
        if (StringUtils.hasText(params.getName())) {
            wrapper.like(Contract::getName, params.getName());
        }
        if (params.getCustomerId() != null) {
            wrapper.eq(Contract::getCustomerId, params.getCustomerId());
        }
        if (params.getOpportunityId() != null) {
            wrapper.eq(Contract::getOpportunityId, params.getOpportunityId());
        }
        if (params.getStatus() != null) {
            wrapper.eq(Contract::getStatus, params.getStatus());
        }
        if (params.getOwnerId() != null) {
            wrapper.eq(Contract::getOwnerId, params.getOwnerId());
        }
        if (params.getSignDateStart() != null) {
            wrapper.ge(Contract::getSignDate, params.getSignDateStart());
        }
        if (params.getSignDateEnd() != null) {
            wrapper.le(Contract::getSignDate, params.getSignDateEnd());
        }
        if (params.getCreateTimeStart() != null) {
            wrapper.ge(Contract::getCreateTime, params.getCreateTimeStart().atStartOfDay());
        }
        if (params.getCreateTimeEnd() != null) {
            wrapper.le(Contract::getCreateTime, params.getCreateTimeEnd().atTime(23, 59, 59));
        }

        wrapper.orderByDesc(Contract::getCreateTime);

        IPage<Contract> contractPage = contractMapper.selectPage(page, wrapper);

        // 转换为DTO
        return contractPage.convert(this::convertToDTO);
    }

    /**
     * 根据客户ID获取合同列表
     *
     * @param customerId 客户ID
     * @return 合同列表
     */
    public List<ContractDTO> getContractsByCustomerId(Long customerId) {
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getCustomerId, customerId)
               .orderByDesc(Contract::getCreateTime);
        List<Contract> contracts = contractMapper.selectList(wrapper);
        return contracts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据商机ID获取合同列表
     *
     * @param opportunityId 商机ID
     * @return 合同列表
     */
    public List<ContractDTO> getContractsByOpportunityId(Long opportunityId) {
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getOpportunityId, opportunityId)
               .orderByDesc(Contract::getCreateTime);
        List<Contract> contracts = contractMapper.selectList(wrapper);
        return contracts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取合同详情
     *
     * @param id 合同ID
     * @return 合同DTO
     */
    public ContractDTO getContractById(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }
        return convertToDTO(contract);
    }

    /**
     * 创建合同
     *
     * @param formData 表单数据
     * @return 合同ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createContract(ContractDTO formData) {
        // 验证客户是否存在
        Customer customer = customerMapper.selectById(formData.getCustomerId());
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        // 验证商机是否存在（如果指定了商机）
        if (formData.getOpportunityId() != null) {
            Opportunity opportunity = opportunityMapper.selectById(formData.getOpportunityId());
            if (opportunity == null) {
                throw new BusinessException("商机不存在");
            }
            if (!opportunity.getCustomerId().equals(formData.getCustomerId())) {
                throw new BusinessException("商机不属于该客户");
            }
        }

        Contract contract = new Contract();
        BeanUtils.copyProperties(formData, contract);
        contract.setId(idGenerator.nextId());

        // 生成合同编号
        if (!StringUtils.hasText(contract.getContractNo())) {
            contract.setContractNo(generateContractNo());
        } else {
            // 检查合同编号是否重复
            LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Contract::getContractNo, contract.getContractNo());
            if (contractMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("合同编号已存在");
            }
        }

        // 设置默认状态为草稿
        if (contract.getStatus() == null) {
            contract.setStatus(STATUS_DRAFT);
        }

        contractMapper.insert(contract);
        log.info("创建合同成功，ID: {}, 编号: {}", contract.getId(), contract.getContractNo());
        return contract.getId();
    }

    /**
     * 从商机创建合同
     *
     * @param opportunityId 商机ID
     * @return 合同ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createFromOpportunity(Long opportunityId) {
        Opportunity opportunity = opportunityMapper.selectById(opportunityId);
        if (opportunity == null) {
            throw new BusinessException("商机不存在");
        }

        ContractDTO formData = new ContractDTO();
        formData.setName(opportunity.getName() + " - 合同");
        formData.setCustomerId(opportunity.getCustomerId());
        formData.setOpportunityId(opportunityId);
        formData.setAmount(opportunity.getAmount());
        formData.setOwnerId(opportunity.getOwnerId());

        return createContract(formData);
    }

    /**
     * 更新合同
     *
     * @param id 合同ID
     * @param formData 表单数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateContract(Long id, ContractDTO formData) {
        Contract existingContract = contractMapper.selectById(id);
        if (existingContract == null) {
            throw new BusinessException("合同不存在");
        }

        // 检查状态是否允许修改
        if (STATUS_COMPLETED.equals(existingContract.getStatus()) ||
            STATUS_TERMINATED.equals(existingContract.getStatus())) {
            throw new BusinessException("已完成或已终止的合同不能修改");
        }

        // 验证客户是否存在
        if (formData.getCustomerId() != null) {
            Customer customer = customerMapper.selectById(formData.getCustomerId());
            if (customer == null) {
                throw new BusinessException("客户不存在");
            }
        }

        // 检查合同编号是否重复（排除自身）
        if (StringUtils.hasText(formData.getContractNo())) {
            LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Contract::getContractNo, formData.getContractNo())
                   .ne(Contract::getId, id);
            if (contractMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("合同编号已存在");
            }
        }

        Contract contract = new Contract();
        BeanUtils.copyProperties(formData, contract);
        contract.setId(id);

        contractMapper.updateById(contract);
        log.info("更新合同成功，ID: {}", id);
    }

    /**
     * 删除合同
     *
     * @param id 合同ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteContract(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        // 只有草稿状态的合同可以删除
        if (!STATUS_DRAFT.equals(contract.getStatus())) {
            throw new BusinessException("只有草稿状态的合同可以删除");
        }

        // TODO: 检查是否有关联的回款记录

        contractMapper.deleteById(id);
        log.info("删除合同成功，ID: {}", id);
    }

    /**
     * 提交审批
     *
     * @param id 合同ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitForApproval(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        if (!STATUS_DRAFT.equals(contract.getStatus())) {
            throw new BusinessException("只有草稿状态的合同可以提交审批");
        }

        Contract updateContract = new Contract();
        updateContract.setId(id);
        updateContract.setStatus(STATUS_APPROVING);
        contractMapper.updateById(updateContract);

        log.info("合同提交审批成功，ID: {}", id);
    }

    /**
     * 审批通过（开始执行）
     *
     * @param id 合同ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        if (!STATUS_APPROVING.equals(contract.getStatus())) {
            throw new BusinessException("只有审批中的合同可以审批通过");
        }

        Contract updateContract = new Contract();
        updateContract.setId(id);
        updateContract.setStatus(STATUS_EXECUTING);
        updateContract.setSignDate(LocalDate.now());
        contractMapper.updateById(updateContract);

        log.info("合同审批通过，ID: {}", id);

        // 发布合同签署事件
        Contract signedContract = contractMapper.selectById(id);
        eventPublisher.publishEvent(new ContractSignedEvent(this, signedContract));
    }

    /**
     * 审批驳回（退回草稿）
     *
     * @param id 合同ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        if (!STATUS_APPROVING.equals(contract.getStatus())) {
            throw new BusinessException("只有审批中的合同可以驳回");
        }

        Contract updateContract = new Contract();
        updateContract.setId(id);
        updateContract.setStatus(STATUS_DRAFT);
        contractMapper.updateById(updateContract);

        log.info("合同审批驳回，ID: {}", id);
    }

    /**
     * 完成合同
     *
     * @param id 合同ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        if (!STATUS_EXECUTING.equals(contract.getStatus())) {
            throw new BusinessException("只有执行中的合同可以完成");
        }

        Contract updateContract = new Contract();
        updateContract.setId(id);
        updateContract.setStatus(STATUS_COMPLETED);
        contractMapper.updateById(updateContract);

        log.info("合同完成，ID: {}", id);
    }

    /**
     * 终止合同
     *
     * @param id 合同ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void terminate(Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        if (!STATUS_EXECUTING.equals(contract.getStatus())) {
            throw new BusinessException("只有执行中的合同可以终止");
        }

        Contract updateContract = new Contract();
        updateContract.setId(id);
        updateContract.setStatus(STATUS_TERMINATED);
        contractMapper.updateById(updateContract);

        log.info("合同终止，ID: {}", id);
    }

    /**
     * 更新合同文件
     *
     * @param id 合同ID
     * @param fileUrl 文件URL
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFileUrl(Long id, String fileUrl) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            throw new BusinessException("合同不存在");
        }

        Contract updateContract = new Contract();
        updateContract.setId(id);
        updateContract.setFileUrl(fileUrl);
        contractMapper.updateById(updateContract);

        log.info("更新合同文件成功，ID: {}", id);
    }

    /**
     * 生成合同编号
     *
     * @return 合同编号
     */
    private String generateContractNo() {
        String prefix = "HT";
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 查询当天最大编号
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Contract::getContractNo, prefix + dateStr)
               .orderByDesc(Contract::getContractNo)
               .last("LIMIT 1");
        Contract lastContract = contractMapper.selectOne(wrapper);

        int sequence = 1;
        if (lastContract != null && lastContract.getContractNo() != null) {
            String lastNo = lastContract.getContractNo();
            String seqStr = lastNo.substring(prefix.length() + dateStr.length());
            try {
                sequence = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                sequence = 1;
            }
        }

        return String.format("%s%s%04d", prefix, dateStr, sequence);
    }

    /**
     * 将实体转换为DTO
     *
     * @param contract 合同实体
     * @return 合同DTO
     */
    private ContractDTO convertToDTO(Contract contract) {
        ContractDTO dto = new ContractDTO();
        BeanUtils.copyProperties(contract, dto);

        // 设置状态名称
        dto.setStatusName(STATUS_NAME_MAP.getOrDefault(contract.getStatus(), "未知"));

        // 加载客户名称
        if (contract.getCustomerId() != null) {
            Customer customer = customerMapper.selectById(contract.getCustomerId());
            if (customer != null) {
                dto.setCustomerName(customer.getName());
            }
        }

        // 加载商机名称
        if (contract.getOpportunityId() != null) {
            Opportunity opportunity = opportunityMapper.selectById(contract.getOpportunityId());
            if (opportunity != null) {
                dto.setOpportunityName(opportunity.getName());
            }
        }

        // 加载负责人名称
        if (contract.getOwnerId() != null) {
            User user = userMapper.selectById(contract.getOwnerId());
            if (user != null) {
                dto.setOwnerName(user.getNickname());
            }
        }

        // TODO: 计算已回款金额和未回款金额

        return dto;
    }

    /**
     * 获取合同统计数据
     *
     * @param ownerId 负责人ID（可选）
     * @return 统计数据
     */
    public Map<String, Object> getContractStatistics(Long ownerId) {
        Map<String, Object> statistics = new HashMap<>();

        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wrapper.eq(Contract::getOwnerId, ownerId);
        }

        // 总合同数
        Long totalCount = contractMapper.selectCount(wrapper);
        statistics.put("totalCount", totalCount);

        // 各状态合同数
        Map<String, Long> statusCount = new HashMap<>();
        for (Map.Entry<Integer, String> entry : STATUS_NAME_MAP.entrySet()) {
            LambdaQueryWrapper<Contract> statusWrapper = new LambdaQueryWrapper<>();
            statusWrapper.eq(Contract::getStatus, entry.getKey());
            if (ownerId != null) {
                statusWrapper.eq(Contract::getOwnerId, ownerId);
            }
            statusCount.put(entry.getValue(), contractMapper.selectCount(statusWrapper));
        }
        statistics.put("statusCount", statusCount);

        // 总金额
        List<Contract> contracts = contractMapper.selectList(wrapper);
        BigDecimal totalAmount = contracts.stream()
                .map(Contract::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("totalAmount", totalAmount);

        return statistics;
    }
}
