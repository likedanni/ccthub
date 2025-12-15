package com.ccthub.userservice.dto;

import java.util.List;

/**
 * 仅用于更新景区媒体字段的请求 DTO
 */
public class ScenicMediaUpdateRequest {
    private String coverImage;
    private List<String> images;

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
