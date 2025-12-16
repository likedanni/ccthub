package com.ccthub.userservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccthub.userservice.dto.ApiResponse;
import com.ccthub.userservice.model.ChinaRegion;
import com.ccthub.userservice.repository.ChinaRegionRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 中国行政区域管理控制器
 * 提供省市区三级行政区域数据查询功能
 */
@Tag(name = "行政区域管理", description = "中国行政区域相关接口，提供省、市、区县三级区域数据查询功能")
@RestController
@RequestMapping("/api/regions")
public class RegionController {

    @Autowired
    private ChinaRegionRepository regionRepository;

    /**
     * 获取所有省份
     */
    @Operation(summary = "获取所有省份", description = "获取中国所有省份/直辖市/自治区/特别行政区列表")
    @GetMapping("/provinces")
    public ApiResponse<List<ChinaRegion>> getProvinces() {
        List<ChinaRegion> provinces = regionRepository.findByLevel(1);
        return ApiResponse.success("获取省份列表成功", provinces);
    }

    /**
     * 根据省份代码获取城市列表
     */
    @Operation(summary = "获取城市列表", description = "根据省份代码获取该省份下的所有城市/地级市列表")
    @GetMapping("/cities")
    public ApiResponse<List<ChinaRegion>> getCitiesByProvince(
            @Parameter(description = "省份代码，例如：110000（北京）、140000（山西）", required = true) @RequestParam String provinceCode) {
        List<ChinaRegion> cities = regionRepository.findByParentCode(provinceCode);
        return ApiResponse.success("获取城市列表成功", cities);
    }

    /**
     * 根据城市代码获取区县列表
     */
    @Operation(summary = "获取区县列表", description = "根据城市代码获取该城市下的所有区县列表")
    @GetMapping("/districts")
    public ApiResponse<List<ChinaRegion>> getDistrictsByCity(
            @Parameter(description = "城市代码，例如：140400（长治市）", required = true) @RequestParam String cityCode) {
        List<ChinaRegion> districts = regionRepository.findByParentCode(cityCode);
        return ApiResponse.success("获取区县列表成功", districts);
    }
}
