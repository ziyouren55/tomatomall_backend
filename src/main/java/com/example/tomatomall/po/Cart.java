package com.example.tomatomall.po;

import com.example.tomatomall.vo.shopping.CartItemVO;
import com.example.tomatomall.vo.shopping.CartItemsVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Cart
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartItemId",
        columnDefinition = "INT COMMENT '购物车商品id'")
    private Integer cartItemId;

    @Column(name = "userId", nullable = false,
        columnDefinition = "INT COMMENT '用户id，关联用户表'")
    private Integer userId;

    @Column(name = "product_id",nullable = false,
        columnDefinition = "INT COMMENT '商品id，关联商品表'")
    private Integer productId;

    @Column(name = "quantity", nullable = false,
        columnDefinition = "INT DEFAULT 1 COMMENT '商品数量，默认为1'")
    private Integer quantity = 1;

    @Column(name = "state",nullable = false)
    private String state = "SHOW";

//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "account",nullable = false,
//            foreignKey = @ForeignKey(name = "fk_user_cart"))
//    private Account account;
//
//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "product", nullable = false,
//            foreignKey = @ForeignKey(name = "fk_product_cart"))
//    private Product product;
}
