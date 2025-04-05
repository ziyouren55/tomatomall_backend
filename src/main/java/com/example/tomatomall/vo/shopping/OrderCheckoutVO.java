package com.example.tomatomall.vo.shopping;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderCheckoutVO
{
    private String username;
    private List<String> cartItemIds;
    private String paymentMethod;
    private ReceiverInfo receiverInfo;
}
