package com.example.tomatomall.vo.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class MemberLevelVO {
    private Integer id;
    private Integer memberLevel;
    private String levelName;
    private Integer pointsRequired;
    private BigDecimal discountRate;
    private String description;
    private Boolean isActive;
}
