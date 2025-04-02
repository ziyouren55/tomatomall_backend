package com.example.tomatomall.vo.shopping;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CartResponse
{
    private List<CartItemResponse> cartItems;
    private Integer total;
    private Double totalAmount;
}
