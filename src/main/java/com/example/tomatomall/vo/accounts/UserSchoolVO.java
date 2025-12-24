package com.example.tomatomall.vo.accounts;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 单独的学校/认证信息 VO，只包含与学校认证相关的字段
 */
@Setter
@Getter
@NoArgsConstructor
public class UserSchoolVO {
    private Boolean schoolCertified;
    private String schoolName;
    private String schoolCode;
    private String cityCode;
    private String cityName;
}


