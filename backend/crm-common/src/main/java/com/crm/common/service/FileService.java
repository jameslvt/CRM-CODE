package com.crm.common.service;

import com.crm.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务
 * 提供文件上传、下载、删除等功能
 * 支持本地存储，可扩展为云存储（OSS、S3等）
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
public class FileService {

    /**
     * 文件存储根路径
     */
    @Value("${crm.file.upload-path:./uploads}")
    private String uploadPath;

    /**
     * 文件访问URL前缀
     */
    @Value("${crm.file.access-url:/api/files}")
    private String accessUrl;

    /**
     * 允许上传的文件类型
     */
    @Value("${crm.file.allowed-types:jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,ppt,pptx,txt,zip,rar}")
    private String allowedTypes;

    /**
     * 最大文件大小（MB）
     */
    @Value("${crm.file.max-size:50}")
    private int maxSizeMB;

    /**
     * 允许的文件扩展名列表
     */
    private List<String> allowedExtensions;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        // 解析允许的文件类型
        allowedExtensions = Arrays.asList(allowedTypes.toLowerCase().split(","));

        // 创建上传目录
        try {
            Path path = Paths.get(uploadPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("创建文件上传目录: {}", path.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("创建文件上传目录失败", e);
            throw new RuntimeException("初始化文件服务失败", e);
        }
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @param subDir 子目录（如: contract、avatar等）
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String subDir) {
        // 验证文件
        validateFile(file);

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFilename = generateFilename(extension);

        // 构建存储路径（按日期分目录）
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = StringUtils.hasText(subDir)
                ? subDir + "/" + datePath + "/" + newFilename
                : datePath + "/" + newFilename;

        Path targetPath = Paths.get(uploadPath, relativePath);

        try {
            // 创建目录
            Files.createDirectories(targetPath.getParent());

            // 保存文件
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("文件上传成功: {} -> {}", originalFilename, targetPath);

            // 返回访问URL
            return accessUrl + "/" + relativePath;

        } catch (IOException e) {
            log.error("文件上传失败: {}", originalFilename, e);
            throw new BusinessException("文件上传失败");
        }
    }

    /**
     * 上传文件（默认目录）
     *
     * @param file 文件
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, null);
    }

    /**
     * 上传合同文件
     *
     * @param file 文件
     * @return 文件访问URL
     */
    public String uploadContractFile(MultipartFile file) {
        return uploadFile(file, "contract");
    }

    /**
     * 上传头像
     *
     * @param file 文件
     * @return 文件访问URL
     */
    public String uploadAvatar(MultipartFile file) {
        // 头像只允许图片
        String extension = getFileExtension(file.getOriginalFilename());
        if (!Arrays.asList("jpg", "jpeg", "png", "gif").contains(extension.toLowerCase())) {
            throw new BusinessException("头像只支持 jpg、jpeg、png、gif 格式");
        }
        return uploadFile(file, "avatar");
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return false;
        }

        // 从URL中提取相对路径
        String relativePath = fileUrl.replace(accessUrl + "/", "");
        Path filePath = Paths.get(uploadPath, relativePath);

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("文件删除成功: {}", filePath);
                return true;
            } else {
                log.warn("文件不存在: {}", filePath);
                return false;
            }
        } catch (IOException e) {
            log.error("文件删除失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 获取文件
     *
     * @param fileUrl 文件URL
     * @return 文件对象
     */
    public File getFile(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            throw new BusinessException("文件URL不能为空");
        }

        String relativePath = fileUrl.replace(accessUrl + "/", "");
        Path filePath = Paths.get(uploadPath, relativePath);

        if (!Files.exists(filePath)) {
            throw new BusinessException("文件不存在");
        }

        return filePath.toFile();
    }

    /**
     * 检查文件是否存在
     *
     * @param fileUrl 文件URL
     * @return 是否存在
     */
    public boolean fileExists(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return false;
        }

        String relativePath = fileUrl.replace(accessUrl + "/", "");
        Path filePath = Paths.get(uploadPath, relativePath);
        return Files.exists(filePath);
    }

    /**
     * 验证文件
     *
     * @param file 文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }

        // 验证文件大小
        long maxSize = (long) maxSizeMB * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小不能超过 " + maxSizeMB + "MB");
        }

        // 验证文件类型
        String extension = getFileExtension(file.getOriginalFilename());
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new BusinessException("不支持的文件类型: " + extension);
        }

        // 验证文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains("..")) {
            throw new BusinessException("文件名包含非法字符");
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名（不含点）
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return filename.substring(dotIndex + 1);
    }

    /**
     * 生成唯一文件名
     *
     * @param extension 扩展名
     * @return 新文件名
     */
    private String generateFilename(String extension) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        if (StringUtils.hasText(extension)) {
            return uuid + "." + extension;
        }
        return uuid;
    }

    /**
     * 获取文件MIME类型
     *
     * @param filename 文件名
     * @return MIME类型
     */
    public String getMimeType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt":
                return "text/plain";
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            default:
                return "application/octet-stream";
        }
    }
}
