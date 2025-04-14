package com.example.tomatomall.vo.shopping;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CartItemsVO
{
    private List<CartItemVO> cartItems;
    private Integer total;
    private Double totalAmount;
}
