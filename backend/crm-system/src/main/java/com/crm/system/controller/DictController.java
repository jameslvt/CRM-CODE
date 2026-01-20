package com.crm.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.system.entity.DictData;
import com.crm.system.entity.DictType;
import com.crm.system.mapper.DictDataMapper;
import com.crm.system.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理控制器
 *
 * @author CRM System
 * @since 2026-01-20
 */
@Api(tags = "字典管理")
@RestController
@RequestMapping("/system/dict")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;
    private final DictDataMapper dictDataMapper;

    /**
     * 分页查询字典类型列表
     */
    @ApiOperation("分页查询字典类型列表")
    @GetMapping("/type/page")
    public Result<PageResult<DictType>> pageDictType(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String dictName,
            @RequestParam(required = false) String dictType,
            @RequestParam(required = false) Integer status) {

        Page<DictType> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(dictName)) {
            wrapper.like(DictType::getName, dictName);
        }
        if (StringUtils.hasText(dictType)) {
            wrapper.like(DictType::getType, dictType);
        }
        if (status != null) {
            wrapper.eq(DictType::getStatus, status);
        }

        wrapper.orderByDesc(DictType::getCreateTime);
        Page<DictType> result = dictService.page(page, wrapper);

        return Result.success(PageResult.of(result));
    }

    /**
     * 根据ID查询字典类型
     */
    @ApiOperation("根据ID查询字典类型")
    @GetMapping("/type/{id}")
    public Result<DictType> getDictTypeById(@PathVariable Long id) {
        return Result.success(dictService.getById(id));
    }

    /**
     * 新增字典类型
     */
    @ApiOperation("新增字典类型")
    @PostMapping("/type")
    public Result<Void> saveDictType(@RequestBody DictType dictType) {
        dictService.saveDictType(dictType);
        return Result.success();
    }

    /**
     * 修改字典类型
     */
    @ApiOperation("修改字典类型")
    @PutMapping("/type")
    public Result<Void> updateDictType(@RequestBody DictType dictType) {
        dictService.updateDictType(dictType);
        return Result.success();
    }

    /**
     * 删除字典类型
     */
    @ApiOperation("删除字典类型")
    @DeleteMapping("/type/{id}")
    public Result<Void> deleteDictType(@PathVariable Long id) {
        dictService.deleteDictType(id);
        return Result.success();
    }

    /**
     * 根据字典类型查询字典数据
     */
    @ApiOperation("根据字典类型查询字典数据")
    @GetMapping("/data/type/{dictType}")
    public Result<List<DictData>> getDictDataByType(@PathVariable String dictType) {
        return Result.success(dictService.getDictDataByType(dictType));
    }

    /**
     * 分页查询字典数据列表
     */
    @ApiOperation("分页查询字典数据列表")
    @GetMapping("/data/page")
    public Result<PageResult<DictData>> pageDictData(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String dictType,
            @RequestParam(required = false) String dictLabel,
            @RequestParam(required = false) Integer status) {

        Page<DictData> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(dictType)) {
            wrapper.eq(DictData::getDictType, dictType);
        }
        if (StringUtils.hasText(dictLabel)) {
            wrapper.like(DictData::getLabel, dictLabel);
        }
        if (status != null) {
            wrapper.eq(DictData::getStatus, status);
        }

        wrapper.orderByAsc(DictData::getSort);
        Page<DictData> result = dictDataMapper.selectPage(page, wrapper);

        return Result.success(PageResult.of(result));
    }

    /**
     * 根据ID查询字典数据
     */
    @ApiOperation("根据ID查询字典数据")
    @GetMapping("/data/{id}")
    public Result<DictData> getDictDataById(@PathVariable Long id) {
        return Result.success(dictDataMapper.selectById(id));
    }

    /**
     * 新增字典数据
     */
    @ApiOperation("新增字典数据")
    @PostMapping("/data")
    public Result<Void> saveDictData(@RequestBody DictData dictData) {
        dictService.saveDictData(dictData);
        return Result.success();
    }

    /**
     * 修改字典数据
     */
    @ApiOperation("修改字典数据")
    @PutMapping("/data")
    public Result<Void> updateDictData(@RequestBody DictData dictData) {
        dictService.updateDictData(dictData);
        return Result.success();
    }

    /**
     * 删除字典数据
     */
    @ApiOperation("删除字典数据")
    @DeleteMapping("/data/{id}")
    public Result<Void> deleteDictData(@PathVariable Long id) {
        dictService.deleteDictData(id);
        return Result.success();
    }

    /**
     * 刷新字典缓存
     */
    @ApiOperation("刷新字典缓存")
    @PostMapping("/refresh/{dictType}")
    public Result<Void> refreshDictCache(@PathVariable String dictType) {
        dictService.clearDictCache(dictType);
        return Result.success();
    }
}
