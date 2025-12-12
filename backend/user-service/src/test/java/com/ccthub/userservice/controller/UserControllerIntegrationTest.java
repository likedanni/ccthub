package com.ccthub.userservice.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ccthub.userservice.config.TestContainersConfig;
import com.ccthub.userservice.dto.LoginRequest;
import com.ccthub.userservice.dto.RegisterRequest;
import com.ccthub.userservice.dto.UpdateProfileRequest;
import com.ccthub.userservice.dto.ChangePasswordRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
public class UserControllerIntegrationTest extends TestContainersConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.ccthub.userservice.repository.UserRepository userRepository;

    @BeforeEach
    public void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    public void testRegister() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setPhone("13800000001");
        request.setPassword("password123");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.phone", equalTo("13800000001")))
                .andExpect(jsonPath("$.access_token", notNullValue()))
                .andExpect(jsonPath("$.refresh_token", notNullValue()))
                .andExpect(jsonPath("$.ai_default_model", equalTo("claude-haiku-4.5")));
    }

    @Test
    public void testLogin() throws Exception {
        // 先注册
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPhone("13800000002");
        registerRequest.setPassword("password123");
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // 再登录
        LoginRequest loginRequest = new LoginRequest("13800000002", "password123");
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone", equalTo("13800000002")))
                .andExpect(jsonPath("$.access_token", notNullValue()));
    }

    @Test
    public void testLoginWithWrongPassword() throws Exception {
        // 先注册
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPhone("13800000003");
        registerRequest.setPassword("password123");
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // 用错误密码登录
        LoginRequest loginRequest = new LoginRequest("13800000003", "wrongpassword");
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void testGetUserProfile() throws Exception {
        // 先注册用户
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPhone("13800000010");
        registerRequest.setPassword("password123");
        String responseBody = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        Long userId = objectMapper.readTree(responseBody).get("id").asLong();
        
        // 获取用户资料
        mockMvc.perform(get("/api/users/" + userId + "/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.phone").value("13800000010"))
                .andExpect(jsonPath("$.memberLevel").value(1));
    }
    
    @Test
    void testUpdateUserProfile() throws Exception {
        // 先注册用户
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPhone("13800000011");
        registerRequest.setPassword("password123");
        String responseBody = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        Long userId = objectMapper.readTree(responseBody).get("id").asLong();
        
        // 更新用户资料
        UpdateProfileRequest updateRequest = new UpdateProfileRequest();
        updateRequest.setNickname("新昵称");
        updateRequest.setAvatarUrl("https://example.com/avatar.jpg");
        
        mockMvc.perform(put("/api/users/" + userId + "/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("新昵称"))
                .andExpect(jsonPath("$.avatarUrl").value("https://example.com/avatar.jpg"));
    }
    
    @Test
    void testChangePassword() throws Exception {
        // 先注册用户
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPhone("13800000012");
        registerRequest.setPassword("password123");
        String responseBody = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        Long userId = objectMapper.readTree(responseBody).get("id").asLong();
        
        // 修改密码
        ChangePasswordRequest changeRequest = new ChangePasswordRequest();
        changeRequest.setOldPassword("password123");
        changeRequest.setNewPassword("newPassword456");
        
        mockMvc.perform(post("/api/users/" + userId + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
                
        // 用新密码登录验证
        LoginRequest loginRequest = new LoginRequest("13800000012", "newPassword456");
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token", notNullValue()));
    }
    
    @Test
    void testPaymentPassword() throws Exception {
        // 先注册用户
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPhone("13800000013");
        registerRequest.setPassword("password123");
        String responseBody = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        Long userId = objectMapper.readTree(responseBody).get("id").asLong();
        
        // 设置支付密码
        Map<String, String> setRequest = Map.of("paymentPassword", "123456");
        mockMvc.perform(post("/api/users/" + userId + "/payment-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(setRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        // 验证正确的支付密码
        Map<String, String> verifyRequest = Map.of("paymentPassword", "123456");
        mockMvc.perform(post("/api/users/" + userId + "/verify-payment-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
                
        // 验证错误的支付密码
        Map<String, String> wrongRequest = Map.of("paymentPassword", "999999");
        mockMvc.perform(post("/api/users/" + userId + "/verify-payment-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}
