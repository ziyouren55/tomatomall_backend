package com.example.tomatomall.controller;


import com.example.tomatomall.service.ImageService;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping
    public Response<String> upload(@RequestParam("file") MultipartFile file, @RequestParam(value = "module", required = false) String module) {
        // 调用服务层处理文件上传
        String url = imageService.upload(file);
        return Response.buildSuccess(url); // 返回上传后图片的 URL
    }
}
