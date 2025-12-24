package com.example.tomatomall.vo.products;

import com.example.tomatomall.enums.RecommendationPriority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecommendProductVO {
    private ProductVO product;
    /**
     * 推荐范围/优先级
     */
    private RecommendationPriority priority;
}


