package com.example.tomatomall.po;


import com.example.tomatomall.vo.products.StockpileVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Stockpile
{
    @Id
    private Integer id;

    @Column(name = "product_id")
    @NotNull
    private Integer productId;

    // 可卖库存
    @NotNull
    private Integer amount;

    // 冻结库存
    @NotNull
    private Integer frozen;

    public StockpileVO toVO()
    {
        StockpileVO stockpileVO = new StockpileVO();
        stockpileVO.setId(id);
        stockpileVO.setFrozen(frozen);
        stockpileVO.setAmount(amount);
        stockpileVO.setProductId(productId);

        return stockpileVO;
    }

}
