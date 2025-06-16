package com.example.tomatomall.vo.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ReportVO {
    private Integer id;
    private Integer userId;
    private Integer targetId;
    private String targetType;
    private String reasonType;
    private String description;
    private String status;
    private Integer processedBy;
    private Date processedTime;
    private Date createTime;

    // 额外需要的字段
    private String username; // 举报用户名
    private String targetContent; // 被举报内容摘要
}
