package com.example.tomatomall.po;


import com.example.tomatomall.vo.products.StockpileVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stockpile")
public class Stockpile
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 使用JPA关联关系，而不是简单的Integer
    @OneToOne
    @JoinColumn(name = "product_id", unique = true, nullable = false)
    private Product product;

    // 可卖库存
    @NotNull
    @Column(name = "amount", nullable = false)
    private Integer amount = 0;

    // 冻结库存
    @NotNull
    @Column(name = "frozen", nullable = false)
    private Integer frozen = 0;

    // 便捷方法：获取可用库存
    public Integer getAvailableAmount() {
        return amount - frozen;
    }

    // 便捷方法：获取商品ID（用于向后兼容）
    public Integer getProductId() {
        return product != null ? product.getId() : null;
    }

    public StockpileVO toVO()
    {
        StockpileVO stockpileVO = new StockpileVO();
        stockpileVO.setId(id);
        stockpileVO.setFrozen(frozen);
        stockpileVO.setAmount(amount);
        if (product != null) {
            stockpileVO.setProductId(product.getId());
            stockpileVO.setProductName(product.getTitle());
        }

        return stockpileVO;
    }

}
