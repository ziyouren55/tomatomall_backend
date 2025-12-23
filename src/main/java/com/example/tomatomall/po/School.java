package com.example.tomatomall.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "school")
public class School {
    // many import scripts use `code` as unique key for school
    @Id
    @Column(name = "code", length = 64)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "supervisor")
    private String supervisor;

    @Column(name = "level")
    private String level;

    @Column(name = "`type`")
    private String type;

    @Column(name = "province_code")
    private String provinceCode;

    @Column(name = "city_code")
    private String cityCode;

    @Column(name = "province_name")
    private String provinceName;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "loc_reason")
    private String locReason;
}


