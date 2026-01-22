package com.crm.controller;

import com.crm.common.result.Result;
import com.crm.common.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 * 提供文件上传、下载、删除等接口
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Api(tags = "文件管理")
public class FileController {

    private final FileService fileService;

    /**
     * 通用文件上传
     *
     * @param file 文件
     * @param subDir 子目录（可选）
     * @return 文件URL
     */
    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public Result<Map<String, String>> uploadFile(
            @ApiParam("文件") @RequestParam("file") MultipartFile file,
            @ApiParam("子目录") @RequestParam(value = "subDir", required = false) String subDir) {

        String fileUrl = fileService.uploadFile(file, subDir);

        Map<String, String> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("name", file.getOriginalFilename());
        result.put("size", String.valueOf(file.getSize()));

        return Result.success(result);
    }

    /**
     * 上传合同文件
     *
     * @param file 文件
     * @return 文件URL
     */
    @PostMapping("/upload/contract")
    @ApiOperation("上传合同文件")
    public Result<Map<String, String>> uploadContractFile(
            @ApiParam("文件") @RequestParam("file") MultipartFile file) {

        String fileUrl = fileService.uploadContractFile(file);

        Map<String, String> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("name", file.getOriginalFilename());
        result.put("size", String.valueOf(file.getSize()));

        return Result.success(result);
    }

    /**
     * 上传头像
     *
     * @param file 文件
     * @return 文件URL
     */
    @PostMapping("/upload/avatar")
    @ApiOperation("上传头像")
    public Result<Map<String, String>> uploadAvatar(
            @ApiParam("文件") @RequestParam("file") MultipartFile file) {

        String fileUrl = fileService.uploadAvatar(file);

        Map<String, String> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("name", file.getOriginalFilename());
        result.put("size", String.valueOf(file.getSize()));

        return Result.success(result);
    }

    /**
     * 下载文件
     *
     * @param path 文件路径（相对路径）
     * @return 文件流
     */
    @GetMapping("/**")
    @ApiOperation("下载文件")
    public ResponseEntity<Resource> downloadFile(
            @ApiParam("文件路径") @RequestParam(value = "download", required = false) Boolean download,
            javax.servlet.http.HttpServletRequest request) {

        // 获取请求路径
        String requestUri = request.getRequestURI();
        String fileUrl = requestUri.replace("/api/files", "/api/files");

        try {
            File file = fileService.getFile(fileUrl);
            Resource resource = new FileSystemResource(file);

            String mimeType = fileService.getMimeType(file.getName());
            String filename = file.getName();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));

            // 如果是下载模式，设置Content-Disposition
            if (Boolean.TRUE.equals(download)) {
                String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
                        .replace("+", "%20");
                headers.add(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedFilename + "\"");
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            log.error("文件下载失败: {}", fileUrl, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    @ApiOperation("删除文件")
    public Result<Boolean> deleteFile(
            @ApiParam("文件URL") @RequestParam("fileUrl") String fileUrl) {

        boolean success = fileService.deleteFile(fileUrl);
        return Result.success(success);
    }

    /**
     * 检查文件是否存在
     *
     * @param fileUrl 文件URL
     * @return 是否存在
     */
    @GetMapping("/exists")
    @ApiOperation("检查文件是否存在")
    public Result<Boolean> fileExists(
            @ApiParam("文件URL") @RequestParam("fileUrl") String fileUrl) {

        boolean exists = fileService.fileExists(fileUrl);
        return Result.success(exists);
    }
}
