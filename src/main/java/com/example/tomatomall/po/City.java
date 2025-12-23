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
@Table(name = "city")
public class City {
    @Id
    @Column(name = "code", length = 32)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "province_code")
    private String provinceCode;
}


