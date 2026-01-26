package com.crm.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.business.dto.OpportunityDTO;
import com.crm.business.dto.OpportunityProductDTO;
import com.crm.business.dto.OpportunityQueryParams;
import com.crm.business.dto.StageAdvanceDTO;
import com.crm.business.entity.Contact;
import com.crm.business.entity.Customer;
import com.crm.business.entity.Opportunity;
import com.crm.business.entity.OpportunityProduct;
import com.crm.business.entity.Product;
import com.crm.business.event.OpportunityWonEvent;
import com.crm.business.mapper.ContactMapper;
import com.crm.business.mapper.CustomerMapper;
import com.crm.business.mapper.OpportunityMapper;
import com.crm.business.mapper.OpportunityProductMapper;
import com.crm.business.mapper.ProductMapper;
import com.crm.common.exception.BusinessException;

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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商机服务
 * 提供商机的CRUD、阶段推进、金额计算等功能
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpportunityService {

    private final OpportunityMapper opportunityMapper;
    private final OpportunityProductMapper opportunityProductMapper;
    private final CustomerMapper customerMapper;
    private final ContactMapper contactMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 商机阶段常量
     */
    public static final String STAGE_REQUIREMENT = "REQUIREMENT";
    public static final String STAGE_PROPOSAL = "PROPOSAL";
    public static final String STAGE_NEGOTIATION = "NEGOTIATION";
    public static final String STAGE_WON = "WON";
    public static final String STAGE_LOST = "LOST";

    /**
     * 阶段对应的赢单概率
     */
    private static final Map<String, Integer> STAGE_PROBABILITY_MAP = new HashMap<>();
    static {
        STAGE_PROBABILITY_MAP.put(STAGE_REQUIREMENT, 20);
        STAGE_PROBABILITY_MAP.put(STAGE_PROPOSAL, 40);
        STAGE_PROBABILITY_MAP.put(STAGE_NEGOTIATION, 60);
        STAGE_PROBABILITY_MAP.put(STAGE_WON, 100);
        STAGE_PROBABILITY_MAP.put(STAGE_LOST, 0);
    }

    /**
     * 分页查询商机列表
     *
     * @param params 查询参数
     * @return 分页结果
     */
    public IPage<OpportunityDTO> getOpportunityPage(OpportunityQueryParams params) {
        Page<Opportunity> page = new Page<>(params.getPageNum(), params.getPageSize());
        LambdaQueryWrapper<Opportunity> wrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (StringUtils.hasText(params.getName())) {
            wrapper.like(Opportunity::getName, params.getName());
        }
        if (params.getCustomerId() != null) {
            wrapper.eq(Opportunity::getCustomerId, params.getCustomerId());
        }
        if (StringUtils.hasText(params.getStage())) {
            wrapper.eq(Opportunity::getStage, params.getStage());
        }
        if (params.getOwnerId() != null) {
            wrapper.eq(Opportunity::getOwnerId, params.getOwnerId());
        }
        if (StringUtils.hasText(params.getSource())) {
            wrapper.eq(Opportunity::getSource, params.getSource());
        }
        if (params.getExpectedDateStart() != null) {
            wrapper.ge(Opportunity::getExpectedDate, params.getExpectedDateStart());
        }
        if (params.getExpectedDateEnd() != null) {
            wrapper.le(Opportunity::getExpectedDate, params.getExpectedDateEnd());
        }
        if (params.getCreateTimeStart() != null) {
            wrapper.ge(Opportunity::getCreateTime, params.getCreateTimeStart().atStartOfDay());
        }
        if (params.getCreateTimeEnd() != null) {
            wrapper.le(Opportunity::getCreateTime, params.getCreateTimeEnd().atTime(23, 59, 59));
        }

        wrapper.orderByDesc(Opportunity::getCreateTime);

        IPage<Opportunity> opportunityPage = opportunityMapper.selectPage(page, wrapper);

        // 转换为DTO
        return opportunityPage.convert(this::convertToDTO);
    }

    /**
     * 根据客户ID获取商机列表
     *
     * @param customerId 客户ID
     * @return 商机列表
     */
    public List<OpportunityDTO> getOpportunitiesByCustomerId(Long customerId) {
        LambdaQueryWrapper<Opportunity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Opportunity::getCustomerId, customerId)
                .orderByDesc(Opportunity::getCreateTime);
        List<Opportunity> opportunities = opportunityMapper.selectList(wrapper);
        return opportunities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取商机详情
     *
     * @param id 商机ID
     * @return 商机DTO
     */
    public OpportunityDTO getOpportunityById(Long id) {
        Opportunity opportunity = opportunityMapper.selectById(id);
        if (opportunity == null) {
            throw new BusinessException("商机不存在");
        }
        OpportunityDTO dto = convertToDTO(opportunity);
        // 加载商机产品
        dto.setProducts(getOpportunityProducts(id));
        return dto;
    }

    /**
     * 创建商机
     *
     * @param formData 表单数据
     * @return 商机ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createOpportunity(OpportunityDTO formData) {
        // 验证客户是否存在
        Customer customer = customerMapper.selectById(formData.getCustomerId());
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        // 验证联系人是否存在（如果指定了联系人）
        if (formData.getContactId() != null) {
            Contact contact = contactMapper.selectById(formData.getContactId());
            if (contact == null) {
                throw new BusinessException("联系人不存在");
            }
            if (!contact.getCustomerId().equals(formData.getCustomerId())) {
                throw new BusinessException("联系人不属于该客户");
            }
        }

        Opportunity opportunity = new Opportunity();
        BeanUtils.copyProperties(formData, opportunity);

        // 设置默认阶段和概率
        if (!StringUtils.hasText(opportunity.getStage())) {
            opportunity.setStage(STAGE_REQUIREMENT);
        }
        opportunity.setProbability(STAGE_PROBABILITY_MAP.getOrDefault(opportunity.getStage(), 0));

        // 设置默认金额
        if (opportunity.getAmount() == null) {
            opportunity.setAmount(BigDecimal.ZERO);
        }

        opportunityMapper.insert(opportunity);

        // 保存商机产品
        if (formData.getProducts() != null && !formData.getProducts().isEmpty()) {
            saveOpportunityProducts(opportunity.getId(), formData.getProducts());
            // 重新计算金额
            recalculateAmount(opportunity.getId());
        }

        log.info("创建商机成功，ID: {}, 名称: {}", opportunity.getId(), opportunity.getName());
        return opportunity.getId();
    }

    /**
     * 更新商机
     *
     * @param id       商机ID
     * @param formData 表单数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOpportunity(Long id, OpportunityDTO formData) {
        Opportunity existingOpportunity = opportunityMapper.selectById(id);
        if (existingOpportunity == null) {
            throw new BusinessException("商机不存在");
        }

        // 检查是否已关闭
        if (STAGE_WON.equals(existingOpportunity.getStage()) || STAGE_LOST.equals(existingOpportunity.getStage())) {
            throw new BusinessException("已关闭的商机不能修改");
        }

        // 验证客户是否存在
        if (formData.getCustomerId() != null) {
            Customer customer = customerMapper.selectById(formData.getCustomerId());
            if (customer == null) {
                throw new BusinessException("客户不存在");
            }
        }

        // 验证联系人是否存在
        if (formData.getContactId() != null) {
            Contact contact = contactMapper.selectById(formData.getContactId());
            if (contact == null) {
                throw new BusinessException("联系人不存在");
            }
            Long customerId = formData.getCustomerId() != null ? formData.getCustomerId()
                    : existingOpportunity.getCustomerId();
            if (!contact.getCustomerId().equals(customerId)) {
                throw new BusinessException("联系人不属于该客户");
            }
        }

        Opportunity opportunity = new Opportunity();
        BeanUtils.copyProperties(formData, opportunity);
        opportunity.setId(id);

        // 如果阶段变更，更新概率
        if (StringUtils.hasText(formData.getStage())) {
            opportunity.setProbability(STAGE_PROBABILITY_MAP.getOrDefault(formData.getStage(), 0));
        }

        opportunityMapper.updateById(opportunity);

        // 更新商机产品
        if (formData.getProducts() != null) {
            // 删除原有产品
            LambdaQueryWrapper<OpportunityProduct> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OpportunityProduct::getOpportunityId, id);
            opportunityProductMapper.delete(wrapper);

            // 保存新产品
            if (!formData.getProducts().isEmpty()) {
                saveOpportunityProducts(id, formData.getProducts());
            }

            // 重新计算金额
            recalculateAmount(id);
        }

        log.info("更新商机成功，ID: {}", id);
    }

    /**
     * 删除商机
     *
     * @param id 商机ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteOpportunity(Long id) {
        Opportunity opportunity = opportunityMapper.selectById(id);
        if (opportunity == null) {
            throw new BusinessException("商机不存在");
        }

        // 删除商机产品
        LambdaQueryWrapper<OpportunityProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OpportunityProduct::getOpportunityId, id);
        opportunityProductMapper.delete(wrapper);

        // 删除商机
        opportunityMapper.deleteById(id);
        log.info("删除商机成功，ID: {}", id);
    }

    /**
     * 推进商机阶段
     *
     * @param advanceDTO 阶段推进DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void advanceStage(StageAdvanceDTO advanceDTO) {
        Opportunity opportunity = opportunityMapper.selectById(advanceDTO.getOpportunityId());
        if (opportunity == null) {
            throw new BusinessException("商机不存在");
        }

        // 检查是否已关闭
        if (STAGE_WON.equals(opportunity.getStage()) || STAGE_LOST.equals(opportunity.getStage())) {
            throw new BusinessException("已关闭的商机不能推进阶段");
        }

        String targetStage = advanceDTO.getTargetStage();

        // 验证阶段有效性
        if (!STAGE_PROBABILITY_MAP.containsKey(targetStage)) {
            throw new BusinessException("无效的目标阶段");
        }

        // 输单时必须填写关闭原因
        if (STAGE_LOST.equals(targetStage) && !StringUtils.hasText(advanceDTO.getCloseReason())) {
            throw new BusinessException("输单时必须填写关闭原因");
        }

        // 更新商机
        Opportunity updateOpportunity = new Opportunity();
        updateOpportunity.setId(opportunity.getId());
        updateOpportunity.setStage(targetStage);
        updateOpportunity.setProbability(STAGE_PROBABILITY_MAP.get(targetStage));

        if (StringUtils.hasText(advanceDTO.getRemark())) {
            updateOpportunity.setRemark(advanceDTO.getRemark());
        }
        if (StringUtils.hasText(advanceDTO.getCloseReason())) {
            updateOpportunity.setCloseReason(advanceDTO.getCloseReason());
        }

        opportunityMapper.updateById(updateOpportunity);

        log.info("商机阶段推进成功，ID: {}, 从 {} 到 {}", opportunity.getId(), opportunity.getStage(), targetStage);

        // 如果赢单，发布事件
        if (STAGE_WON.equals(targetStage)) {
            Opportunity wonOpportunity = opportunityMapper.selectById(opportunity.getId());
            eventPublisher.publishEvent(new OpportunityWonEvent(this, wonOpportunity));
            log.info("发布商机赢单事件，商机ID: {}", opportunity.getId());
        }
    }

    /**
     * 获取商机产品列表
     *
     * @param opportunityId 商机ID
     * @return 商机产品列表
     */
    public List<OpportunityProductDTO> getOpportunityProducts(Long opportunityId) {
        LambdaQueryWrapper<OpportunityProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OpportunityProduct::getOpportunityId, opportunityId);
        List<OpportunityProduct> products = opportunityProductMapper.selectList(wrapper);

        return products.stream().map(op -> {
            OpportunityProductDTO dto = new OpportunityProductDTO();
            BeanUtils.copyProperties(op, dto);

            // 加载产品信息
            Product product = productMapper.selectById(op.getProductId());
            if (product != null) {
                dto.setProductName(product.getName());
                dto.setProductCode(product.getCode());
                dto.setUnit(product.getUnit());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 保存商机产品
     *
     * @param opportunityId 商机ID
     * @param products      产品列表
     */
    private void saveOpportunityProducts(Long opportunityId, List<OpportunityProductDTO> products) {
        for (OpportunityProductDTO productDTO : products) {
            // 验证产品是否存在
            Product product = productMapper.selectById(productDTO.getProductId());
            if (product == null) {
                throw new BusinessException("产品不存在: " + productDTO.getProductId());
            }

            OpportunityProduct op = new OpportunityProduct();
            op.setOpportunityId(opportunityId);
            op.setProductId(productDTO.getProductId());
            op.setQuantity(productDTO.getQuantity() != null ? productDTO.getQuantity() : 1);
            op.setPrice(productDTO.getPrice() != null ? productDTO.getPrice() : product.getPrice());
            op.setDiscount(productDTO.getDiscount() != null ? productDTO.getDiscount() : new BigDecimal("100"));

            // 计算金额: 数量 * 单价 * 折扣 / 100
            BigDecimal amount = op.getPrice()
                    .multiply(new BigDecimal(op.getQuantity()))
                    .multiply(op.getDiscount())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            op.setAmount(amount);

            opportunityProductMapper.insert(op);
        }
    }

    /**
     * 重新计算商机金额
     *
     * @param opportunityId 商机ID
     */
    private void recalculateAmount(Long opportunityId) {
        LambdaQueryWrapper<OpportunityProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OpportunityProduct::getOpportunityId, opportunityId);
        List<OpportunityProduct> products = opportunityProductMapper.selectList(wrapper);

        BigDecimal totalAmount = products.stream()
                .map(OpportunityProduct::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Opportunity updateOpportunity = new Opportunity();
        updateOpportunity.setId(opportunityId);
        updateOpportunity.setAmount(totalAmount);
        opportunityMapper.updateById(updateOpportunity);
    }

    /**
     * 将实体转换为DTO
     *
     * @param opportunity 商机实体
     * @return 商机DTO
     */
    private OpportunityDTO convertToDTO(Opportunity opportunity) {
        OpportunityDTO dto = new OpportunityDTO();
        BeanUtils.copyProperties(opportunity, dto);

        // 加载客户名称
        if (opportunity.getCustomerId() != null) {
            Customer customer = customerMapper.selectById(opportunity.getCustomerId());
            if (customer != null) {
                dto.setCustomerName(customer.getName());
            }
        }

        // 加载联系人名称
        if (opportunity.getContactId() != null) {
            Contact contact = contactMapper.selectById(opportunity.getContactId());
            if (contact != null) {
                dto.setContactName(contact.getName());
            }
        }

        // 加载负责人名称
        if (opportunity.getOwnerId() != null) {
            User user = userMapper.selectById(opportunity.getOwnerId());
            if (user != null) {
                dto.setOwnerName(user.getNickname());
            }
        }

        // 设置阶段名称
        dto.setStageName(opportunity.getStage());

        return dto;
    }

    /**
     * 获取商机统计数据
     *
     * @param ownerId 负责人ID（可选）
     * @return 统计数据
     */
    public Map<String, Object> getOpportunityStatistics(Long ownerId) {
        Map<String, Object> statistics = new HashMap<>();

        LambdaQueryWrapper<Opportunity> wrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wrapper.eq(Opportunity::getOwnerId, ownerId);
        }

        // 总商机数
        Long totalCount = opportunityMapper.selectCount(wrapper);
        statistics.put("totalCount", totalCount);

        // 各阶段商机数
        Map<String, Long> stageCount = new HashMap<>();
        for (String stage : STAGE_PROBABILITY_MAP.keySet()) {
            LambdaQueryWrapper<Opportunity> stageWrapper = new LambdaQueryWrapper<>();
            stageWrapper.eq(Opportunity::getStage, stage);
            if (ownerId != null) {
                stageWrapper.eq(Opportunity::getOwnerId, ownerId);
            }
            stageCount.put(stage, opportunityMapper.selectCount(stageWrapper));
        }
        statistics.put("stageCount", stageCount);

        // 总金额
        List<Opportunity> opportunities = opportunityMapper.selectList(wrapper);
        BigDecimal totalAmount = opportunities.stream()
                .map(Opportunity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("totalAmount", totalAmount);

        // 赢单金额
        LambdaQueryWrapper<Opportunity> wonWrapper = new LambdaQueryWrapper<>();
        wonWrapper.eq(Opportunity::getStage, STAGE_WON);
        if (ownerId != null) {
            wonWrapper.eq(Opportunity::getOwnerId, ownerId);
        }
        List<Opportunity> wonOpportunities = opportunityMapper.selectList(wonWrapper);
        BigDecimal wonAmount = wonOpportunities.stream()
                .map(Opportunity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("wonAmount", wonAmount);

        return statistics;
    }
}
