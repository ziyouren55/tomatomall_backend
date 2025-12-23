package com.example.tomatomall.vo.accounts;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class SchoolVerificationVO {

    private Integer id;
    private Integer userId;
    private String schoolName;
    // accept studentId on request but do not expose in responses
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    private String studentId;
    // do NOT expose studentId in responses
    private String certificateUrl;
    private String status;
    private Date submittedAt;
    private Date verifiedAt;
    private String rejectedReason;

}


