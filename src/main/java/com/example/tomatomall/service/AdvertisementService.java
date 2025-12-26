package com.example.tomatomall.service;

import com.example.tomatomall.vo.Advertisement.AdvertisementVO;
import java.util.List;

public interface AdvertisementService {
    List<AdvertisementVO> getAllAdvertisements();

    String updateAdvertisement(String id,String title,String content,String imgUrl,String productId);

    AdvertisementVO createAdvertisement(String title,String content,String imgUrl,String productId);

    String deleteAdvertisement(String id);
}
