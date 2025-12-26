package com.example.tomatomall.controller;

import com.example.tomatomall.service.AdvertisementService;
import com.example.tomatomall.vo.Advertisement.AdvertisementVO;
import com.example.tomatomall.vo.Response;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/advertisements")

public class AdvertisementController {
    @Autowired
    AdvertisementService advertisementService;

    @GetMapping
    public Response<List<AdvertisementVO>> getAllAdvertisements(){
        return Response.buildSuccess(advertisementService.getAllAdvertisements());
    }

    @PutMapping
    public Response<String> updateAdvertisement(@RequestBody AdvertisementVO vo) {
        return Response.buildSuccess(advertisementService.updateAdvertisement(
                vo.getId(), vo.getTitle(), vo.getContent(), vo.getImgUrl(), vo.getProductId()));
    }


    @PostMapping
    public Response<AdvertisementVO> createAdvertisement(@RequestBody AdvertisementVO vo) {
        return Response.buildSuccess(advertisementService.createAdvertisement(
                vo.getTitle(), vo.getContent(), vo.getImgUrl(), vo.getProductId()));
    }


    @DeleteMapping("/{id}")
    public Response<String> deleteAdvertisement(@PathVariable String id){
        return Response.buildSuccess(advertisementService.deleteAdvertisement(id));
    }
}
