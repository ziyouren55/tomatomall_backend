package com.example.tomatomall.controller;

import com.example.tomatomall.enums.BusinessError;
import com.example.tomatomall.po.School;
import com.example.tomatomall.repository.SchoolRepository;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/locations/schools")
public class AdminLocationController {

    @Autowired
    private SchoolRepository schoolRepository;

    @GetMapping
    public Response<Page<School>> page(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size,
                                      @RequestParam(required = false) String q,
                                      @RequestParam(required = false, name = "city_code") String cityCode) {
        PageRequest pr = PageRequest.of(Math.max(0, page), Math.max(1, size));
        org.springframework.data.domain.Page<School> pageResult;
        if (cityCode != null && !cityCode.trim().isEmpty()) {
            if (q != null && !q.trim().isEmpty()) {
                pageResult = schoolRepository.findByCityCodeAndNameContainingIgnoreCase(cityCode, q.trim(), pr);
            } else {
                pageResult = schoolRepository.findByCityCodeOrderByName(cityCode, pr);
            }
        } else {
            if (q != null && !q.trim().isEmpty()) {
                pageResult = schoolRepository.findByNameContainingIgnoreCase(q.trim(), pr);
            } else {
                pageResult = schoolRepository.findAll(pr);
            }
        }
        return Response.buildSuccess(pageResult);
    }

    @PostMapping
    public Response<School> create(@RequestBody School s) {
        School saved = schoolRepository.save(s);
        return Response.buildSuccess(saved);
    }

    @PutMapping("/{code}")
    public Response<School> update(@PathVariable("code") String code, @RequestBody School s) {
        s.setCode(code);
        School saved = schoolRepository.save(s);
        return Response.buildSuccess(saved);
    }

    @DeleteMapping("/{code}")
    public Response<String> delete(@PathVariable("code") String code) {
        schoolRepository.deleteById(code);
        return Response.buildSuccess("删除成功");
    }

    @PostMapping("/import")
    public Response<java.util.Map<String,Object>> importCsv(@RequestParam("file") MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int success = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // skip header
                if (line.trim().isEmpty()) continue;
                // naive CSV split, assumes simple CSV without quoted commas
                String[] cols = line.split(",", -1);
                try {
                    // map columns leniently: code,name,supervisor,level,type,province_code,city_code,province_name,city_name,loc_reason
                    String code = cols.length > 0 ? cols[0].trim() : "";
                    String name = cols.length > 1 ? cols[1].trim() : "";
                    School s = new School();
                    s.setCode(code);
                    s.setName(name);
                    if (cols.length > 2) s.setSupervisor(cols[2].trim());
                    if (cols.length > 3) s.setLevel(cols[3].trim());
                    if (cols.length > 4) s.setType(cols[4].trim());
                    if (cols.length > 5) s.setProvinceCode(cols[5].trim());
                    if (cols.length > 6) s.setCityCode(cols[6].trim());
                    if (cols.length > 7) s.setProvinceName(cols[7].trim());
                    if (cols.length > 8) s.setCityName(cols[8].trim());
                    if (cols.length > 9) s.setLocReason(cols[9].trim());
                    schoolRepository.save(s);
                    success++;
                } catch (Exception ex) {
                    errors.add("line failed: " + line + " -> " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            return Response.buildFailure(BusinessError.IMPORT_FAILED.getCode(), "导入失败: " + e.getMessage());
        }
        java.util.Map<String, Object> res = new java.util.HashMap<>();
        res.put("success", success);
        res.put("errors", errors);
        return Response.buildSuccess(res);
    }
}


