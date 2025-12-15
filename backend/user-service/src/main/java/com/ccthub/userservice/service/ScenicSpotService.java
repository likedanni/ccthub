package com.ccthub.userservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.dto.PageResponse;
import com.ccthub.userservice.dto.ScenicSpotDetailResponse;
import com.ccthub.userservice.dto.ScenicSpotRequest;
import com.ccthub.userservice.dto.ScenicSpotResponse;
import com.ccthub.userservice.model.ScenicSpot;
import com.ccthub.userservice.repository.ScenicSpotRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ScenicSpotService {

    @Autowired
    private ScenicSpotRepository scenicSpotRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 获取景区列表（分页+筛选）
     */
    public PageResponse<ScenicSpotResponse> getScenicSpotList(
            String name, String province, String city, String level, String status, Pageable pageable) {

        Page<ScenicSpot> page;

        // 根据不同条件组合查询
        if (name != null && !name.isEmpty() && status != null && !status.isEmpty()) {
            page = scenicSpotRepository.findByNameContainingAndStatus(name, status, pageable);
        } else if (name != null && !name.isEmpty()) {
            page = scenicSpotRepository.findByNameContaining(name, pageable);
        } else if (status != null && !status.isEmpty() && city != null && !city.isEmpty()) {
            page = scenicSpotRepository.findByStatusAndCity(status, city, pageable);
        } else if (status != null && !status.isEmpty() && province != null && !province.isEmpty()) {
            page = scenicSpotRepository.findByStatusAndProvince(status, province, pageable);
        } else if (status != null && !status.isEmpty() && level != null && !level.isEmpty()) {
            page = scenicSpotRepository.findByStatusAndLevel(status, level, pageable);
        } else if (status != null && !status.isEmpty()) {
            page = scenicSpotRepository.findByStatus(status, pageable);
        } else if (city != null && !city.isEmpty()) {
            page = scenicSpotRepository.findByCity(city, pageable);
        } else if (province != null && !province.isEmpty()) {
            page = scenicSpotRepository.findByProvince(province, pageable);
        } else if (level != null && !level.isEmpty()) {
            page = scenicSpotRepository.findByLevel(level, pageable);
        } else {
            page = scenicSpotRepository.findAll(pageable);
        }

        return convertToPageResponse(page);
    }

    /**
     * 获取景区详情
     */
    public ScenicSpotDetailResponse getScenicSpotDetail(Long id) throws Exception {
        ScenicSpot scenicSpot = scenicSpotRepository.findById(id)
                .orElseThrow(() -> new Exception("景区不存在"));

        // 增加浏览次数
        scenicSpot.setViewCount(scenicSpot.getViewCount() + 1);
        scenicSpotRepository.save(scenicSpot);

        return convertToDetailResponse(scenicSpot);
    }

    /**
     * 创建景区
     */
    @Transactional
    public ScenicSpotDetailResponse createScenicSpot(ScenicSpotRequest request) throws Exception {
        ScenicSpot scenicSpot = new ScenicSpot();
        updateScenicSpotFromRequest(scenicSpot, request);

        ScenicSpot saved = scenicSpotRepository.save(scenicSpot);
        return convertToDetailResponse(saved);
    }

    /**
     * 更新景区
     */
    @Transactional
    public ScenicSpotDetailResponse updateScenicSpot(Long id, ScenicSpotRequest request) throws Exception {
        ScenicSpot scenicSpot = scenicSpotRepository.findById(id)
                .orElseThrow(() -> new Exception("景区不存在"));

        updateScenicSpotFromRequest(scenicSpot, request);

        ScenicSpot updated = scenicSpotRepository.save(scenicSpot);
        return convertToDetailResponse(updated);
    }

    /**
     * 更新景区状态
     */
    @Transactional
    public void updateScenicSpotStatus(Long id, String status) throws Exception {
        ScenicSpot scenicSpot = scenicSpotRepository.findById(id)
                .orElseThrow(() -> new Exception("景区不存在"));

        scenicSpot.setStatus(status);
        scenicSpotRepository.save(scenicSpot);
    }

    /**
     * 删除景区
     */
    @Transactional
    public void deleteScenicSpot(Long id) throws Exception {
        if (!scenicSpotRepository.existsById(id)) {
            throw new Exception("景区不存在");
        }
        scenicSpotRepository.deleteById(id);
    }

    /**
     * 从请求DTO更新景区实体
     */
    private void updateScenicSpotFromRequest(ScenicSpot scenicSpot, ScenicSpotRequest request)
            throws JsonProcessingException {
        scenicSpot.setName(request.getName());
        scenicSpot.setLevel(request.getLevel());
        scenicSpot.setIntroduction(request.getIntroduction());
        scenicSpot.setProvince(request.getProvince());
        scenicSpot.setCity(request.getCity());
        scenicSpot.setDistrict(request.getDistrict());
        scenicSpot.setAddress(request.getAddress());
        scenicSpot.setLongitude(request.getLongitude());
        scenicSpot.setLatitude(request.getLatitude());
        scenicSpot.setOpeningHours(request.getOpeningHours());
        scenicSpot.setContactPhone(request.getContactPhone());
        scenicSpot.setCoverImage(request.getCoverImage());
        scenicSpot.setTicketInfo(request.getTicketInfo());
        scenicSpot.setTrafficInfo(request.getTrafficInfo());
        scenicSpot.setNotice(request.getNotice());

        if (request.getStatus() != null) {
            scenicSpot.setStatus(request.getStatus());
        }

        // 转换JSON字段
        if (request.getImages() != null) {
            scenicSpot.setImages(objectMapper.writeValueAsString(request.getImages()));
        }
        if (request.getTags() != null) {
            scenicSpot.setTags(objectMapper.writeValueAsString(request.getTags()));
        }
        if (request.getFacilities() != null) {
            scenicSpot.setFacilities(objectMapper.writeValueAsString(request.getFacilities()));
        }
    }

    /**
     * 转换为分页响应
     */
    private PageResponse<ScenicSpotResponse> convertToPageResponse(Page<ScenicSpot> page) {
        List<ScenicSpotResponse> content = new ArrayList<>();

        for (ScenicSpot scenicSpot : page.getContent()) {
            ScenicSpotResponse response = new ScenicSpotResponse();
            response.setId(scenicSpot.getId());
            response.setName(scenicSpot.getName());
            response.setLevel(scenicSpot.getLevel());
            response.setProvince(scenicSpot.getProvince());
            response.setCity(scenicSpot.getCity());
            response.setDistrict(scenicSpot.getDistrict());
            response.setAddress(scenicSpot.getAddress());
            response.setCoverImage(scenicSpot.getCoverImage());
            response.setStatus(scenicSpot.getStatus());
            response.setViewCount(scenicSpot.getViewCount());
            response.setCreateTime(scenicSpot.getCreateTime());

            // 解析tags JSON
            if (scenicSpot.getTags() != null) {
                try {
                    response.setTags(objectMapper.readValue(scenicSpot.getTags(), List.class));
                } catch (JsonProcessingException e) {
                    response.setTags(new ArrayList<>());
                }
            }

            content.add(response);
        }

        PageResponse<ScenicSpotResponse> pageResponse = new PageResponse<>();
        pageResponse.setContent(content);
        pageResponse.setPage(page.getNumber());
        pageResponse.setPageSize(page.getSize());
        pageResponse.setTotal(page.getTotalElements());
        pageResponse.setTotalPages(page.getTotalPages());

        return pageResponse;
    }

    /**
     * 转换为详情响应
     */
    private ScenicSpotDetailResponse convertToDetailResponse(ScenicSpot scenicSpot) throws JsonProcessingException {
        ScenicSpotDetailResponse response = new ScenicSpotDetailResponse();
        response.setId(scenicSpot.getId());
        response.setName(scenicSpot.getName());
        response.setLevel(scenicSpot.getLevel());
        response.setIntroduction(scenicSpot.getIntroduction());
        response.setProvince(scenicSpot.getProvince());
        response.setCity(scenicSpot.getCity());
        response.setDistrict(scenicSpot.getDistrict());
        response.setAddress(scenicSpot.getAddress());
        response.setLongitude(scenicSpot.getLongitude());
        response.setLatitude(scenicSpot.getLatitude());
        response.setOpeningHours(scenicSpot.getOpeningHours());
        response.setContactPhone(scenicSpot.getContactPhone());
        response.setCoverImage(scenicSpot.getCoverImage());
        response.setTicketInfo(scenicSpot.getTicketInfo());
        response.setTrafficInfo(scenicSpot.getTrafficInfo());
        response.setNotice(scenicSpot.getNotice());
        response.setStatus(scenicSpot.getStatus());
        response.setViewCount(scenicSpot.getViewCount());
        response.setCreateTime(scenicSpot.getCreateTime());
        response.setUpdateTime(scenicSpot.getUpdateTime());

        // 解析JSON字段
        if (scenicSpot.getImages() != null) {
            response.setImages(objectMapper.readValue(scenicSpot.getImages(), List.class));
        }
        if (scenicSpot.getTags() != null) {
            response.setTags(objectMapper.readValue(scenicSpot.getTags(), List.class));
        }
        if (scenicSpot.getFacilities() != null) {
            response.setFacilities(objectMapper.readValue(scenicSpot.getFacilities(), List.class));
        }

        return response;
    }
}
