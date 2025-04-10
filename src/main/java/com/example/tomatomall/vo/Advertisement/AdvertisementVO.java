package com.example.tomatomall.vo.Advertisement;

import com.example.tomatomall.po.Advertisements;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdvertisementVO {
    private String id;
    private String title;
    private String content;
    private String imgUrl;
    private String productId;

    public Advertisements toPO(){
        Advertisements advertisements = new Advertisements();
        advertisements.setId(Integer.valueOf(id));
        advertisements.setTitle(title);
        advertisements.setContent(content);
        advertisements.setImageUrl(imgUrl);
        advertisements.setProductId(Integer.valueOf(productId));
        return advertisements;
    }
}
