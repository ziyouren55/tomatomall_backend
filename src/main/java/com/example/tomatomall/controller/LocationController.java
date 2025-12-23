package com.example.tomatomall.controller;

import com.example.tomatomall.po.Province;
import com.example.tomatomall.po.City;
import com.example.tomatomall.po.School;
import com.example.tomatomall.service.LocationService;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/provinces")
    public Response<List<Province>> provinces() {
        return Response.buildSuccess(locationService.listProvinces());
    }

    @GetMapping("/cities")
    public Response<List<City>> cities(@RequestParam("province_code") String provinceCode) {
        return Response.buildSuccess(locationService.listCitiesByProvince(provinceCode));
    }

    @GetMapping("/cities/all")
    public Response<List<City>> allCities() {
        return Response.buildSuccess(locationService.listAllCities());
    }

    @GetMapping("/schools")
    public Response<List<School>> schools(@RequestParam("city_code") String cityCode,
                                         @RequestParam(value = "q", required = false) String q,
                                         @RequestParam(value = "limit", required = false, defaultValue = "50") Integer limit) {
        return Response.buildSuccess(locationService.searchSchools(cityCode, q, limit));
    }
}


