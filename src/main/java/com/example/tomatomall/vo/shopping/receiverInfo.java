package com.example.tomatomall.vo.shopping;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class receiverInfo
{
    private Integer userId;
    private String receiverName;
    private String phone;
    private String zipCode;
    private String address;
}
