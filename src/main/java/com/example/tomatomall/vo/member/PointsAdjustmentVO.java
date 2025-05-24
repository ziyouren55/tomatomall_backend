package com.example.tomatomall.vo.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PointsAdjustmentVO {
    private Integer userId;
    private Integer pointsChange;
    private String reason;
}
