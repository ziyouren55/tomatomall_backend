package com.example.tomatomall.service.serviceImpl;


import com.example.tomatomall.service.ImageService;
import com.example.tomatomall.util.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageServiceImpl implements ImageService
{

    @Autowired
    private OssUtil ossUtil;

    @Override
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空！");
        }

        String originalFilename = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + originalFilename; // 文件名可以加入时间戳防止重复

        try {
            return ossUtil.upload(fileName, file.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败！");
        }
    }
}
