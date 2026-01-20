package com.crm.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.entity.DictData;
import com.crm.system.entity.DictType;
import com.crm.system.mapper.DictDataMapper;
import com.crm.system.mapper.DictTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典服务实现
 *
 * @author CRM System
 * @since 2026-01-20
 */
@Service
@RequiredArgsConstructor
public class DictService extends ServiceImpl<DictTypeMapper, DictType> {

    private final DictDataMapper dictDataMapper;

    /**
     * 根据字典类型查询字典数据（带缓存）
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @Cacheable(value = "dict", key = "#dictType")
    public List<DictData> getDictDataByType(String dictType) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dictType)
                .eq(DictData::getStatus, 1)
                .orderByAsc(DictData::getDictSort);
        return dictDataMapper.selectList(wrapper);
    }

    /**
     * 新增字典类型
     *
     * @param dictType 字典类型
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDictType(DictType dictType) {
        return save(dictType);
    }

    /**
     * 修改字典类型
     *
     * @param dictType 字典类型
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", key = "#dictType.dictType")
    public boolean updateDictType(DictType dictType) {
        return updateById(dictType);
    }

    /**
     * 删除字典类型
     *
     * @param id 字典类型ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictType(Long id) {
        // 查询字典类型
        DictType dictType = getById(id);
        if (dictType != null) {
            // 删除字典类型
            removeById(id);
            // 删除对应的字典数据
            LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DictData::getDictType, dictType.getDictType());
            dictDataMapper.delete(wrapper);
            // 清除缓存
            clearDictCache(dictType.getDictType());
        }
        return true;
    }

    /**
     * 新增字典数据
     *
     * @param dictData 字典数据
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", key = "#dictData.dictType")
    public boolean saveDictData(DictData dictData) {
        return dictDataMapper.insert(dictData) > 0;
    }

    /**
     * 修改字典数据
     *
     * @param dictData 字典数据
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dict", key = "#dictData.dictType")
    public boolean updateDictData(DictData dictData) {
        return dictDataMapper.updateById(dictData) > 0;
    }

    /**
     * 删除字典数据
     *
     * @param id 字典数据ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictData(Long id) {
        DictData dictData = dictDataMapper.selectById(id);
        if (dictData != null) {
            dictDataMapper.deleteById(id);
            clearDictCache(dictData.getDictType());
        }
        return true;
    }

    /**
     * 清除字典缓存
     *
     * @param dictType 字典类型
     */
    @CacheEvict(value = "dict", key = "#dictType")
    public void clearDictCache(String dictType) {
        // 缓存已通过注解清除
    }
}
