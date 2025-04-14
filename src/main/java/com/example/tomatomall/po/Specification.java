package com.example.tomatomall.po;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Specification
{
    @Id
    private Integer id;

    @Column(nullable = false)
    @NotBlank
    private String item;

    @Column(nullable = false)
    @NotBlank
    private String value;

    // 多对一关联，规格必须属于一个商品
    @Column(name = "product_id", nullable = false)
    @NotBlank
    private Integer productId;

}
