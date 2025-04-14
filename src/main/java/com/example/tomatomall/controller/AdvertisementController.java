package com.example.tomatomall.controller;

import com.example.tomatomall.service.AdvertisementService;
import com.example.tomatomall.vo.Advertisement.AdvertisementVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/advertisements")

public class AdvertisementController {
    @Autowired
    AdvertisementService advertisementService;

    @GetMapping
    public Response getAllAdvertisements(){
        return Response.buildSuccess(advertisementService.getAllAdvertisements());
    }

    @PutMapping
    public Response updateAdvertisement(@RequestBody AdvertisementVO vo) {
        return Response.buildSuccess(advertisementService.updateAdvertisement(
                vo.getId(), vo.getTitle(), vo.getContent(), vo.getImgUrl(), vo.getProductId()));
    }


    @PostMapping
    public Response createAdvertisement(@RequestBody AdvertisementVO vo) {
        return Response.buildSuccess(advertisementService.createAdvertisement(
                vo.getTitle(), vo.getContent(), vo.getImgUrl(), vo.getProductId()));
    }


    @DeleteMapping("/{id}")
    public Response deleteAdvertisement(@PathVariable String id){
        return Response.buildSuccess(Response.buildSuccess(advertisementService.deleteAdvertisement(id)));
    }
}
