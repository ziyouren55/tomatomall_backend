package com.example.tomatomall.vo.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class MemberPointsVO {
    private Integer userId;
    private Integer currentPoints;
    private Integer totalPoints;
    private Integer currentLevelId;
    private String currentLevelName;
    private Date updateTime;
}
