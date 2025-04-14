package com.example.tomatomall.po;

import com.example.tomatomall.vo.Advertisement.AdvertisementVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Advertisements")

public class Advertisements {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",columnDefinition = "INT COMMENT '广告id'")
    Integer id;

    @Column(name = "title",nullable = false, columnDefinition = "VARCHAR(50) COMMENT '广告标题，不允许为空'")
    String title;

    @Column(name = "content", nullable = false,columnDefinition = "VARCHAR(500) COMMENT '广告内容'" )
    String content;

    @Column(name = "image_url", nullable = false, columnDefinition = "VARCHAR(500) COMMENT '广告图片url'")
    String imageUrl;

    @Column(name = "product_id" ,nullable = false,columnDefinition = "INT COMMENT '所属商品id，不允许为空'")
    Integer productId;

    public AdvertisementVO toVO(){
        AdvertisementVO advertisementVO = new AdvertisementVO();
        advertisementVO.setId(String.valueOf(id));
        advertisementVO.setTitle(title);
        advertisementVO.setContent(content);
        advertisementVO.setImgUrl(imageUrl);
        advertisementVO.setProductId(String.valueOf(productId));
        return advertisementVO;
    }
}
