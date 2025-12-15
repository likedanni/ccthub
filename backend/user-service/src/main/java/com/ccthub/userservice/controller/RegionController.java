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

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    @Autowired
    private ChinaRegionRepository regionRepository;

    /**
     * 获取所有省份
     */
    @GetMapping("/provinces")
    public ApiResponse<List<ChinaRegion>> getProvinces() {
        List<ChinaRegion> provinces = regionRepository.findByLevel(1);
        return ApiResponse.success("获取省份列表成功", provinces);
    }

    /**
     * 根据省份代码获取城市列表
     */
    @GetMapping("/cities")
    public ApiResponse<List<ChinaRegion>> getCitiesByProvince(@RequestParam String provinceCode) {
        List<ChinaRegion> cities = regionRepository.findByParentCode(provinceCode);
        return ApiResponse.success("获取城市列表成功", cities);
    }

    /**
     * 根据城市代码获取区县列表
     */
    @GetMapping("/districts")
    public ApiResponse<List<ChinaRegion>> getDistrictsByCity(@RequestParam String cityCode) {
        List<ChinaRegion> districts = regionRepository.findByParentCode(cityCode);
        return ApiResponse.success("获取区县列表成功", districts);
    }
}
