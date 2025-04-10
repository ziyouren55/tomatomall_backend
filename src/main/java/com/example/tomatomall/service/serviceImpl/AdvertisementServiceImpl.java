package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Advertisements;
import com.example.tomatomall.repository.AdvertisementRepository;
import com.example.tomatomall.service.AdvertisementService;
import com.example.tomatomall.vo.Advertisement.AdvertisementVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {
    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Override
    public List<AdvertisementVO> getAllAdvertisements() {
        return advertisementRepository.findAll()
                .stream()
                .map(Advertisements::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public String updateAdvertisement(String id,String title,String content,String imgUrl,String productId) {
        Optional<Advertisements> advertisements = advertisementRepository.findById(Integer.valueOf(id));
        if (advertisements.isPresent()){
            advertisements.get().setTitle(title);
            advertisements.get().setContent(content);
            advertisements.get().setImageUrl(imgUrl);
            advertisements.get().setProductId(Integer.valueOf(productId));
            advertisementRepository.save(advertisements.get());
        } else {
            throw  TomatoMallException.advertisementNotFind();
        }
        return "更新成功";
    }

    @Override
    public AdvertisementVO createAdvertisement(String title, String content, String imgUrl, String productId) {
        Advertisements advertisements = new Advertisements();
        advertisements.setProductId(Integer.valueOf(productId));
        advertisements.setTitle(title);
        advertisements.setContent(content);
        advertisements.setImageUrl(imgUrl);
        advertisementRepository.save(advertisements);
        return advertisements.toVO();
    }

    @Override
    public String deleteAdvertisement(String id) {
        Optional<Advertisements> advertisements = advertisementRepository.findById(Integer.valueOf(id));
        if (advertisements.isPresent()){
            advertisementRepository.delete(advertisements.get());
        } else {
            throw TomatoMallException.advertisementNotFind();
        }
        return "删除成功";
    }
}
