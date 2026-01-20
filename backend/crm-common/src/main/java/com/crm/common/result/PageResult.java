package com.crm.common.result;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装类
 * 用于封装分页查询的结果数据
 *
 * @param <T> 数据列表的元素类型
 * @author CRM System
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页显示条数
     */
    private Long size;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 构造函数（自动计算总页数）
     *
     * @param records 数据列表
     * @param total   总记录数
     * @param current 当前页码
     * @param size    每页显示条数
     */
    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        // 计算总页数
        this.pages = (total + size - 1) / size;
    }

    /**
     * 静态工厂方法：创建分页结果
     *
     * @param records 数据列表
     * @param total   总记录数
     * @param current 当前页码
     * @param size    每页显示条数
     * @param <T>     数据类型
     * @return PageResult 对象
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Long current, Long size) {
        return new PageResult<>(records, total, current, size);
    }

    /**
     * 静态工厂方法：从 MyBatis Plus Page 对象创建分页结果
     *
     * @param page MyBatis Plus 分页对象
     * @param <T>  数据类型
     * @return PageResult 对象
     */
    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 判断是否有上一页
     *
     * @return true: 有上一页, false: 无上一页
     */
    public boolean hasPrevious() {
        return this.current != null && this.current > 1;
    }

    /**
     * 判断是否有下一页
     *
     * @return true: 有下一页, false: 无下一页
     */
    public boolean hasNext() {
        return this.current != null && this.pages != null && this.current < this.pages;
    }

    /**
     * 判断是否为空
     *
     * @return true: 空, false: 非空
     */
    public boolean isEmpty() {
        return this.records == null || this.records.isEmpty();
    }
}
