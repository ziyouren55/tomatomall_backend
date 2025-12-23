package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.Province;
import com.example.tomatomall.po.City;
import com.example.tomatomall.po.School;
import com.example.tomatomall.repository.ProvinceRepository;
import com.example.tomatomall.repository.CityRepository;
import com.example.tomatomall.repository.SchoolRepository;
import com.example.tomatomall.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Override
    public List<Province> listProvinces() {
        return provinceRepository.findAll();
    }

    @Override
    public List<City> listCitiesByProvince(String provinceCode) {
        if (provinceCode == null) return java.util.Collections.emptyList();
        return cityRepository.findByProvinceCodeOrderByName(provinceCode);
    }

    @Override
    public List<School> searchSchools(String cityCode, String q, int limit) {
        PageRequest pg = PageRequest.of(0, Math.max(1, limit));
        if (cityCode != null && !cityCode.trim().isEmpty()) {
            if (q == null || q.trim().isEmpty()) {
                return schoolRepository.findByCityCodeOrderByName(cityCode, pg).getContent();
            } else {
                return schoolRepository.findByCityCodeAndNameContainingIgnoreCase(cityCode, q.trim(), pg).getContent();
            }
        } else {
            if (q == null || q.trim().isEmpty()) {
                return schoolRepository.findAll(pg).getContent();
            } else {
                return schoolRepository.findByNameContainingIgnoreCase(q.trim(), pg).getContent();
            }
        }
    }

    @Override
    public List<City> listAllCities() {
        return cityRepository.findAll();
    }
}


