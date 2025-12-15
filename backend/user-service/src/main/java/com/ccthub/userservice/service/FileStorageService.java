package com.ccthub.userservice.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务
 */
@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${file.upload-dir:uploads/avatars}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * 上传头像文件
     * 
     * @param file 上传的文件
     * @return 文件访问URL
     */
    public String uploadAvatar(MultipartFile file) throws IOException {
        logger.info("开始上传头像: filename={}, size={}, contentType={}",
                file.getOriginalFilename(), file.getSize(), file.getContentType());

        // 验证文件
        try {
            validateFile(file);
        } catch (IOException e) {
            logger.error("文件验证失败: {}", e.getMessage());
            throw e;
        }

        // 创建上传目录
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成唯一文件名
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + fileExtension;

        // 保存文件
        Path targetPath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // 返回文件URL
        String fileUrl = baseUrl + "/api/files/avatars/" + newFilename;
        logger.info("头像上传成功: {} -> {}", originalFilename, fileUrl);
        return fileUrl;
    }

    /**
     * 验证上传文件
     */
    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("上传文件不能为空");
        }

        // 验证文件大小 (最大2MB)
        long maxSize = 2 * 1024 * 1024; // 2MB
        if (file.getSize() > maxSize) {
            throw new IOException("文件大小不能超过2MB,当前文件: " + file.getSize() + " bytes");
        }

        // 验证文件类型 - 支持多种MIME类型
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        // 检查MIME类型
        boolean validMimeType = contentType != null && (contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/webp") ||
                contentType.startsWith("image/") // 更宽松的验证
        );

        // 检查文件扩展名
        boolean validExtension = originalFilename != null && (originalFilename.toLowerCase().endsWith(".jpg") ||
                originalFilename.toLowerCase().endsWith(".jpeg") ||
                originalFilename.toLowerCase().endsWith(".png") ||
                originalFilename.toLowerCase().endsWith(".webp"));

        if (!validMimeType && !validExtension) {
            throw new IOException(
                    "只支持JPG、JPEG、PNG、WEBP格式的图片 (当前类型: " + contentType + ", 文件名: " + originalFilename + ")");
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileUrl) {
        try {
            // 从URL中提取文件名
            String filename = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            Path filePath = Paths.get(uploadDir).resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            // 静默处理删除失败
        }
    }
}
