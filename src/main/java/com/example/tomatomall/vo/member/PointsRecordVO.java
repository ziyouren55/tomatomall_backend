package com.example.tomatomall.vo.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class PointsRecordVO {
    private Integer id;
    private Integer userId;
    private Integer pointsChange;
    private String recordType;
    private Integer referenceId;
    private String description;
    private Date createTime;
}
