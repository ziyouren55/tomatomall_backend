package com.example.tomatomall.vo.products;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecommendSearchResultVO {
    private List<RecommendProductVO> products;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;

    public RecommendSearchResultVO(List<RecommendProductVO> products, Long total, Integer page, Integer pageSize) {
        this.products = products;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }
}


