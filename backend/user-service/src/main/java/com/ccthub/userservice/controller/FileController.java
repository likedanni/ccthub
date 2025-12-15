package com.ccthub.userservice.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ccthub.userservice.dto.ApiResponse;
import com.ccthub.userservice.service.FileStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 文件管理控制器
 */
@Tag(name = "文件管理", description = "文件上传、下载等API")
@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Operation(summary = "上传头像", description = "上传用户头像图片,支持JPG、JPEG、PNG格式,最大2MB")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "上传成功,返回图片URL"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "文件格式或大小不符合要求")
    })
    @PostMapping(value = "/upload/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @Parameter(description = "头像图片文件", required = true) @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileStorageService.uploadAvatar(file);
            return ResponseEntity.ok(ApiResponse.success("上传成功", fileUrl));
        } catch (IOException e) {
            logger.error("Upload avatar error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "获取头像图片", description = "根据文件名获取头像图片")
    @GetMapping("/avatars/{filename:.+}")
    public ResponseEntity<Resource> getAvatar(
            @Parameter(description = "文件名", required = true) @PathVariable String filename) {
        try {
            Path avatarPath = Paths.get(uploadDir, "avatars").resolve(filename).normalize();
            Resource resource = new UrlResource(avatarPath.toUri());

            if (!(resource.exists() && resource.isReadable())) {
                // 如果 avatars 目录没有，尝试在 uploadDir 下递归查找同名文件以兼容误存放的情况
                Path found = findFileInUploadDir(filename);
                if (found != null) {
                    resource = new UrlResource(found.toUri());
                    avatarPath = found;
                }
            }

            if (resource.exists() && resource.isReadable()) {
                // 确定文件类型
                String contentType = Files.probeContentType(avatarPath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Get avatar error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "通用文件上传", description = "上传文件并按类别保存, 可选参数 category: avatars/scenic/activity/feedback/others")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> uploadFile(
            @Parameter(description = "文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "类别", required = false) @RequestParam(value = "category", required = false) String category) {
        try {
            String fileUrl = fileStorageService.upload(file, category);
            // 兼容历史返回格式：直接返回 data 为字符串 URL（与 /upload/avatar 保持一致）
            return ResponseEntity.ok(ApiResponse.success("上传成功", fileUrl));
        } catch (IOException e) {
            logger.error("Upload file error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @GetMapping("/{category}/{filename:.+}")
    public ResponseEntity<Resource> getFileByCategory(
            @Parameter(description = "类别", required = true) @PathVariable String category,
            @Parameter(description = "文件名", required = true) @PathVariable String filename) {
        try {
            // 防止路径穿越
            String safeCategory = category.replaceAll("[\\\\/]+", "");
            Path filePath = Paths.get(uploadDir, safeCategory).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!(resource.exists() && resource.isReadable())) {
                // 如果指定类别目录中找不到，尝试在 uploadDir 下递归查找同名文件（兼容历史数据不一致）
                Path found = findFileInUploadDir(filename);
                if (found != null) {
                    filePath = found;
                    resource = new UrlResource(found.toUri());
                }
            }

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Get file by category error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 在 uploadDir 下递归查找文件名相同的文件，返回第一个匹配路径或 null
     */
    private Path findFileInUploadDir(String filename) {
        try {
            Path base = Paths.get(uploadDir);
            if (!Files.exists(base))
                return null;
            try (java.util.stream.Stream<Path> stream = Files.walk(base)) {
                return stream.filter(p -> Files.isRegularFile(p) && p.getFileName().toString().equals(filename))
                        .findFirst().orElse(null);
            }
        } catch (Exception e) {
            logger.warn("查找上传目录中文件失败: {}", e.getMessage());
            return null;
        }
    }
}
