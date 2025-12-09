package com.example.blog.dto;

import lombok.Data;
import java.util.List;

/**
 * 分页结果数据传输对象
 * 
 * 封装分页查询的结果数据，包括记录列表和分页信息
 * 
 * @param <T> 记录数据的泛型类型
 */
@Data
public class PageResult<T> {
    /** 当前页的记录列表 */
    private List<T> records;
    
    /** 总记录数 */
    private Long total;
    
    /** 每页大小 */
    private Integer size;
    
    /** 当前页码 */
    private Integer current;
    
    /** 总页数 */
    private Integer pages;

    /**
     * 创建分页结果对象
     * 
     * @param <T> 记录数据类型
     * @param records 当前页的记录列表
     * @param total 总记录数
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页结果对象
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Integer current, Integer size) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setCurrent(current);
        result.setSize(size);
        result.setPages((int) Math.ceil((double) total / size));
        return result;
    }
}