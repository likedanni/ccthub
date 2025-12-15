package com.ccthub.userservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.dto.ApiResponse;
import com.ccthub.userservice.dto.PageResponse;
import com.ccthub.userservice.dto.ScenicMediaUpdateRequest;
import com.ccthub.userservice.dto.ScenicSpotDetailResponse;
import com.ccthub.userservice.dto.ScenicSpotRequest;
import com.ccthub.userservice.dto.ScenicSpotResponse;
import com.ccthub.userservice.service.ScenicSpotService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/scenic-spots")
@Tag(name = "景区管理", description = "景区信息管理接口")
public class ScenicSpotController {

    @Autowired
    private ScenicSpotService scenicSpotService;

    @GetMapping("/list")
    @Operation(summary = "获取景区列表", description = "支持分页和多条件筛选")
    public ApiResponse<PageResponse<ScenicSpotResponse>> getScenicSpotList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));

        PageResponse<ScenicSpotResponse> result = scenicSpotService.getScenicSpotList(
                name, province, city, level, status, pageable);

        return ApiResponse.success("获取成功", result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取景区详情")
    public ApiResponse<ScenicSpotDetailResponse> getScenicSpotDetail(@PathVariable Long id) {
        try {
            ScenicSpotDetailResponse result = scenicSpotService.getScenicSpotDetail(id);
            return ApiResponse.success("获取成功", result);
        } catch (Exception e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "创建景区", description = "管理员创建新景区")
    public ApiResponse<ScenicSpotDetailResponse> createScenicSpot(
            @RequestBody ScenicSpotRequest request) {
        try {
            ScenicSpotDetailResponse result = scenicSpotService.createScenicSpot(request);
            return ApiResponse.success("创建成功", result);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新景区", description = "管理员更新景区信息")
    public ApiResponse<ScenicSpotDetailResponse> updateScenicSpot(
            @PathVariable Long id,
            @RequestBody ScenicSpotRequest request) {
        try {
            ScenicSpotDetailResponse result = scenicSpotService.updateScenicSpot(id, request);
            return ApiResponse.success("更新成功", result);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/{id}/media")
    @Operation(summary = "更新景区媒体字段", description = "仅更新封面与图册字段")
    public ApiResponse<ScenicSpotDetailResponse> updateScenicMedia(
            @PathVariable Long id,
            @RequestBody ScenicMediaUpdateRequest request) {
        try {
            ScenicSpotDetailResponse result = scenicSpotService.updateScenicMedia(id, request.getCoverImage(),
                    request.getImages());
            return ApiResponse.success("更新成功", result);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新景区状态", description = "管理员更新景区开放/关闭状态")
    public ApiResponse<Void> updateScenicSpotStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            scenicSpotService.updateScenicSpotStatus(id, status);
            return ApiResponse.success("状态更新成功", null);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除景区", description = "管理员删除景区")
    public ApiResponse<Void> deleteScenicSpot(@PathVariable Long id) {
        try {
            scenicSpotService.deleteScenicSpot(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
