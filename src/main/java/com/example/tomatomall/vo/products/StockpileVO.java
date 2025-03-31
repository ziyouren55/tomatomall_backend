package com.example.tomatomall.vo.products;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class StockpileVO
{
    private String id;
    private String productId;
    private Integer amount;
    private Integer frozen;

}
