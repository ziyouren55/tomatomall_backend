package com.example.tomatomall.vo.products;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SpecificationVO
{
    private Integer id;
    private String item;
    private String value;
    private Integer productId;
}
