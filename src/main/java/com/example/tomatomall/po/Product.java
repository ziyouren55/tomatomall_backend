package com.example.tomatomall.po;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @NotNull(message = "商品名称不能为空")
    @Column(name = "title", nullable = false)
    private String title;

    @Basic
    @NotNull(message = "商品名称不能为空")
    @Column(name = "price", nullable = false)
    private Double price;

    @Basic
    @NotNull(message = "商品评分不能为空")
    @Min(value = 0, message = "评分不能低于0")
    @Max(value = 10, message = "评分不能超过10")
    @Column(name = "rate", nullable = false)
    private Float rate;

    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "cover")
    private String cover;

    @Basic
    @Column(name = "detail")
    private String detail;

    @Basic
    @ElementCollection
    @CollectionTable(name = "product_specifications", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "specifications")
    private Set<Specification> specifications;



}
