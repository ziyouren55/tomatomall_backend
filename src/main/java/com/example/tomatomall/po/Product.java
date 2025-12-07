package com.example.tomatomall.po;


import com.example.tomatomall.vo.products.ProductVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

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
    @NotNull(message = "商品价格不能为空")
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

    // 在Product.java中添加销量属性
    @Basic
    @Column(name = "sales_count")
    private Integer salesCount = 0; // 默认销量为0

    // 商品与库存的一对一关联关系
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Stockpile stockpile;

    //todo specification的处理
//   @ElementCollection
//   @CollectionTable(name = "product_specifications", joinColumns = @JoinColumn(name = "product_id"))
//   @Column(name = "specifications")
//   private Set<Specification> specifications;

    // 便捷方法：获取库存数量
    public Integer getStockAmount() {
        return stockpile != null ? stockpile.getAmount() : 0;
    }

    // 便捷方法：获取可用库存（总库存 - 冻结库存）
    public Integer getAvailableStock() {
        if (stockpile == null) return 0;
        return stockpile.getAmount() - stockpile.getFrozen();
    }

    public ProductVO toVO()
    {
        ProductVO productVO = new ProductVO();
        productVO.setId(id);
        productVO.setTitle(title);
        productVO.setPrice(price);
        productVO.setRate(rate);
        productVO.setDescription(description);
        productVO.setCover(cover);
        productVO.setDetail(detail);
        productVO.setSalesCount(salesCount);
//        productVO.setSpecifications(specifications);

        return productVO;
    }

}
