package com.example.tomatomall.po;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Stockpile
{
    @Id
    private String id;

    @Column(name = "product_id")
    @NotBlank
    private String productId;

    // 可卖库存
    @NotNull
    private Integer amount;

    // 冻结库存
    @NotNull
    @NotNull
    private Integer frozen;

}
