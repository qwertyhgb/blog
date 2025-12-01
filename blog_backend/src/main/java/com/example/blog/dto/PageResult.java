package com.example.blog.dto;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> records;
    private Long total;
    private Integer size;
    private Integer current;
    private Integer pages;

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
