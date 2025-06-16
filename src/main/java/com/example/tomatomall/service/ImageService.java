package com.example.tomatomall.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    /**
     * 上传图片文件
     * @param file 图片文件
     * @return 返回图片的可访问 URL
     */
    String upload(MultipartFile file);
}
