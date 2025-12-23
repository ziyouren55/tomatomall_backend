package com.example.tomatomall.service;

import com.example.tomatomall.po.Province;
import com.example.tomatomall.po.City;
import com.example.tomatomall.po.School;

import java.util.List;

public interface LocationService {
    List<Province> listProvinces();
    List<City> listCitiesByProvince(String provinceCode);
    List<School> searchSchools(String cityCode, String q, int limit);
    List<City> listAllCities();
}


