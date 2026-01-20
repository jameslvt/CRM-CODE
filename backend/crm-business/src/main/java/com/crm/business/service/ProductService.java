package com.crm.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.business.dto.ProductDTO;
import com.crm.business.entity.Product;
import com.crm.business.mapper.ProductMapper;
import com.crm.common.exception.BusinessException;
import com.crm.common.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 产品服务
 * 提供产品的CRUD和状态管理
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final IdGenerator idGenerator;

    /**
     * 产品状态：停用
     */
    private static final Integer STATUS_DISABLED = 0;

    /**
     * 产品状态：启用
     */
    private static final Integer STATUS_ENABLED = 1;

    /**
     * 分页查询产品列表
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param name 产品名称（模糊查询）
     * @param code 产品编码（模糊查询）
     * @param category 分类
     * @param status 状态
     * @return 分页结果
     */
    public IPage<ProductDTO> getProductPage(Integer pageNum, Integer pageSize,
                                            String name, String code,
                                            String category, Integer status) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (StringUtils.hasText(name)) {
            wrapper.like(Product::getName, name);
        }
        if (StringUtils.hasText(code)) {
            wrapper.like(Product::getCode, code);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq(Product::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }

        wrapper.orderByDesc(Product::getCreateTime);

        IPage<Product> productPage = productMapper.selectPage(page, wrapper);

        // 转换为DTO
        return productPage.convert(this::convertToDTO);
    }

    /**
     * 获取所有启用的产品列表
     *
     * @return 产品列表
     */
    public List<ProductDTO> getEnabledProducts() {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, STATUS_ENABLED)
               .orderByAsc(Product::getName);
        List<Product> products = productMapper.selectList(wrapper);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取产品
     *
     * @param id 产品ID
     * @return 产品DTO
     */
    public ProductDTO getProductById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        return convertToDTO(product);
    }

    /**
     * 创建产品
     *
     * @param formData 表单数据
     * @return 产品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createProduct(ProductDTO formData) {
        // 检查产品编码是否重复
        if (StringUtils.hasText(formData.getCode())) {
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Product::getCode, formData.getCode());
            if (productMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("产品编码已存在");
            }
        }

        Product product = new Product();
        BeanUtils.copyProperties(formData, product);
        product.setId(idGenerator.nextId());

        // 设置默认状态为启用
        if (product.getStatus() == null) {
            product.setStatus(STATUS_ENABLED);
        }

        productMapper.insert(product);
        log.info("创建产品成功，ID: {}, 名称: {}", product.getId(), product.getName());
        return product.getId();
    }

    /**
     * 更新产品
     *
     * @param id 产品ID
     * @param formData 表单数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Long id, ProductDTO formData) {
        Product existingProduct = productMapper.selectById(id);
        if (existingProduct == null) {
            throw new BusinessException("产品不存在");
        }

        // 检查产品编码是否重复（排除自身）
        if (StringUtils.hasText(formData.getCode())) {
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Product::getCode, formData.getCode())
                   .ne(Product::getId, id);
            if (productMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("产品编码已存在");
            }
        }

        Product product = new Product();
        BeanUtils.copyProperties(formData, product);
        product.setId(id);

        productMapper.updateById(product);
        log.info("更新产品成功，ID: {}", id);
    }

    /**
     * 删除产品
     *
     * @param id 产品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }

        // TODO: 检查产品是否被商机引用，如果被引用则不允许删除

        productMapper.deleteById(id);
        log.info("删除产品成功，ID: {}", id);
    }

    /**
     * 启用产品
     *
     * @param id 产品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void enableProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }

        if (STATUS_ENABLED.equals(product.getStatus())) {
            throw new BusinessException("产品已是启用状态");
        }

        Product updateProduct = new Product();
        updateProduct.setId(id);
        updateProduct.setStatus(STATUS_ENABLED);
        productMapper.updateById(updateProduct);

        log.info("启用产品成功，ID: {}", id);
    }

    /**
     * 停用产品
     *
     * @param id 产品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void disableProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }

        if (STATUS_DISABLED.equals(product.getStatus())) {
            throw new BusinessException("产品已是停用状态");
        }

        Product updateProduct = new Product();
        updateProduct.setId(id);
        updateProduct.setStatus(STATUS_DISABLED);
        productMapper.updateById(updateProduct);

        log.info("停用产品成功，ID: {}", id);
    }

    /**
     * 批量删除产品
     *
     * @param ids 产品ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteProducts(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // TODO: 检查产品是否被商机引用

        productMapper.deleteBatchIds(ids);
        log.info("批量删除产品成功，数量: {}", ids.size());
    }

    /**
     * 将实体转换为DTO
     *
     * @param product 产品实体
     * @return 产品DTO
     */
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);

        // 设置状态名称
        if (STATUS_ENABLED.equals(product.getStatus())) {
            dto.setStatusName("启用");
        } else if (STATUS_DISABLED.equals(product.getStatus())) {
            dto.setStatusName("停用");
        }

        return dto;
    }
}
