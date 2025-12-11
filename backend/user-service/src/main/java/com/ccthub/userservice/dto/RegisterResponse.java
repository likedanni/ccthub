package com.ccthub.userservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterResponse {
    private Long id;
    private String phone;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("ai_default_model")
    private String aiDefaultModel;

    public RegisterResponse(Long id, String phone, String accessToken, String refreshToken, String aiDefaultModel) {
        this.id = id;
        this.phone = phone;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.aiDefaultModel = aiDefaultModel;
    }

    public Long getId() { return id; }
    public String getPhone() { return phone; }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getAiDefaultModel() { return aiDefaultModel; }
}
}
