package com.example.tomatomall.vo.products;


import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Stockpile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class StockpileVO
{
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer amount;
    private Integer frozen;

    // 注意：由于Stockpile现在使用JPA关联关系，toPO方法需要Product对象
    // 这个方法现在主要用于创建新库存记录，需要先获取Product对象
    public Stockpile toPO(Product product)
    {
        Stockpile stockpile = new Stockpile();
        stockpile.setId(id);
        stockpile.setProduct(product); // 使用关联关系
        stockpile.setAmount(amount);
        stockpile.setFrozen(frozen);
        return stockpile;
    }
    
    // 向后兼容的方法（已废弃）
    @Deprecated
    public Stockpile toPO()
    {
        // 这个方法现在无法正确工作，因为Stockpile需要Product对象
        // 建议使用toPO(Product product)方法
        throw new UnsupportedOperationException("请使用toPO(Product product)方法，需要提供Product对象");
    }
}
