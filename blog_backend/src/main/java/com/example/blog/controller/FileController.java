package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.error("请选择要上传的文件");
        }

        try {
            // 确保目录存在
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // 保存文件
            File dest = new File(directory.getAbsolutePath() + File.separator + newFilename);
            file.transferTo(dest);

            // 返回访问URL（注意：不要包含/api前缀，因为静态资源映射在根路径）
            String fileUrl = "http://localhost:8080/api/uploads/" + newFilename;
            return ApiResponse.success("上传成功", fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }
}
