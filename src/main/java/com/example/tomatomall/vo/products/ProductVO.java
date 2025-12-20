package com.example.tomatomall.vo.products;

import com.example.tomatomall.po.BookComment;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Specification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
@Setter
@Getter
@NoArgsConstructor
public class ProductVO
{
    private Integer id;
    private String title;
    private Double price;
    private Float rate;
    private String description;
    private String cover;
    private String detail;
    private Integer salesCount;
    private Integer storeId; // 店铺ID

    public Product toPO()
    {
        Product product = new Product();
        product.setId(id);
        product.setTitle(title);
        product.setPrice(price);
        product.setRate(rate);
        product.setDescription(description);
        product.setCover(cover);
        product.setDetail(detail);
        product.setSalesCount(salesCount);
        product.setStoreId(storeId);
//        product.setSpecifications(specifications);

        return product;
    }
}
