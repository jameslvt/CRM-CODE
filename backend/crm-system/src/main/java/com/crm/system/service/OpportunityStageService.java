package com.crm.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.exception.BusinessException;
import com.crm.system.entity.OpportunityStage;
import com.crm.system.mapper.OpportunityStageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商机阶段配置服务实现
 *
 * @author CRM System
 * @since 2026-01-20
 */
@Service
@RequiredArgsConstructor
public class OpportunityStageService extends ServiceImpl<OpportunityStageMapper, OpportunityStage> {

    /**
     * 查询所有商机阶段（按排序号升序）
     *
     * @return 商机阶段列表
     */
    public List<OpportunityStage> listAllStages() {
        LambdaQueryWrapper<OpportunityStage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OpportunityStage::getStatus, 1)
                .orderByAsc(OpportunityStage::getSort);
        return list(wrapper);
    }

    /**
     * 新增商机阶段
     *
     * @param stage 商机阶段
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveStage(OpportunityStage stage) {
        // 检查阶段编码是否重复
        LambdaQueryWrapper<OpportunityStage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OpportunityStage::getStageCode, stage.getStageCode());
        if (count(wrapper) > 0) {
            throw new BusinessException("阶段编码已存在");
        }
        return save(stage);
    }

    /**
     * 修改商机阶段
     *
     * @param stage 商机阶段
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStage(OpportunityStage stage) {
        // 检查阶段编码是否重复（排除自己）
        LambdaQueryWrapper<OpportunityStage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OpportunityStage::getStageCode, stage.getStageCode())
                .ne(OpportunityStage::getId, stage.getId());
        if (count(wrapper) > 0) {
            throw new BusinessException("阶段编码已存在");
        }
        return updateById(stage);
    }

    /**
     * 删除商机阶段
     *
     * @param id 阶段ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStage(Long id) {
        // TODO: 检查是否有商机使用该阶段，如果有则不允许删除
        return removeById(id);
    }

    /**
     * 调整阶段排序
     *
     * @param id 阶段ID
     * @param sort 新的排序号
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSort(Long id, Integer sort) {
        OpportunityStage stage = getById(id);
        if (stage == null) {
            throw new BusinessException("商机阶段不存在");
        }
        stage.setSort(sort);
        return updateById(stage);
    }
}
