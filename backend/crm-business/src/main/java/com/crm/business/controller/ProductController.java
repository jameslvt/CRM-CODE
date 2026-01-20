package com.crm.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crm.business.dto.ProductDTO;
import com.crm.business.service.ProductService;
import com.crm.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品管理控制器
 *
 * @author CRM System
 * @since 1.0.0
 */
@Api(tags = "产品管理")
@RestController
@RequestMapping("/api/business/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 分页查询产品列表
     */
    @ApiOperation("分页查询产品列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('business:product:list')")
    public Result<IPage<ProductDTO>> getProductPage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("产品名称") @RequestParam(required = false) String name,
            @ApiParam("产品编码") @RequestParam(required = false) String code,
            @ApiParam("分类") @RequestParam(required = false) String category,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        IPage<ProductDTO> page = productService.getProductPage(pageNum, pageSize, name, code, category, status);
        return Result.success(page);
    }

    /**
     * 获取所有启用的产品列表
     */
    @ApiOperation("获取所有启用的产品列表")
    @GetMapping("/enabled")
    @PreAuthorize("hasAuthority('business:product:list')")
    public Result<List<ProductDTO>> getEnabledProducts() {
        List<ProductDTO> products = productService.getEnabledProducts();
        return Result.success(products);
    }

    /**
     * 根据ID获取产品详情
     */
    @ApiOperation("根据ID获取产品详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('business:product:list')")
    public Result<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return Result.success(product);
    }

    /**
     * 创建产品
     */
    @ApiOperation("创建产品")
    @PostMapping
    @PreAuthorize("hasAuthority('business:product:add')")
    public Result<Long> createProduct(@Validated @RequestBody ProductDTO formData) {
        Long id = productService.createProduct(formData);
        return Result.success(id);
    }

    /**
     * 更新产品
     */
    @ApiOperation("更新产品")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('business:product:edit')")
    public Result<Void> updateProduct(@PathVariable Long id, @Validated @RequestBody ProductDTO formData) {
        productService.updateProduct(id, formData);
        return Result.success();
    }

    /**
     * 删除产品
     */
    @ApiOperation("删除产品")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('business:product:delete')")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    /**
     * 批量删除产品
     */
    @ApiOperation("批量删除产品")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('business:product:delete')")
    public Result<Void> batchDeleteProducts(@RequestBody List<Long> ids) {
        productService.batchDeleteProducts(ids);
        return Result.success();
    }

    /**
     * 启用产品
     */
    @ApiOperation("启用产品")
    @PutMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('business:product:edit')")
    public Result<Void> enableProduct(@PathVariable Long id) {
        productService.enableProduct(id);
        return Result.success();
    }

    /**
     * 停用产品
     */
    @ApiOperation("停用产品")
    @PutMapping("/{id}/disable")
    @PreAuthorize("hasAuthority('business:product:edit')")
    public Result<Void> disableProduct(@PathVariable Long id) {
        productService.disableProduct(id);
        return Result.success();
    }
}
