package com.example.tomatomall.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 通用分页结果VO
 * @param <T> 数据类型
 */
@Getter
@Setter
@NoArgsConstructor
public class PageResultVO<T> {
    private List<T> data;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;

    public PageResultVO(List<T> data, Long total, Integer page, Integer pageSize) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }
}

