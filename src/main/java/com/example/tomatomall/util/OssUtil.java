package com.example.tomatomall.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    /**
     * 上传文件到阿里云 OSS
     * @param objectName   上传到 OSS 上的文件名
     * @param inputStream  文件输入流
     * @return 文件的可访问 URL
     */
    public String upload(String objectName, InputStream inputStream) {
        // 创建 OSSClient 实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // 创建 PutObjectRequest 对象
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            // 上传文件
            ossClient.putObject(putObjectRequest);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        // 生成一个带签名的URL，并截取掉多余参数，获取外链
        return ossClient.generatePresignedUrl(bucketName, objectName, new Date(System.currentTimeMillis() + 3600L * 1000))
                .toString()
                .split("\\?Expires")[0];
    }
}
