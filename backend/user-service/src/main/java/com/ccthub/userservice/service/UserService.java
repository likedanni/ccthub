package com.ccthub.userservice.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ccthub.userservice.dto.LoginRequest;
import com.ccthub.userservice.dto.RegisterRequest;
import com.ccthub.userservice.dto.RegisterResponse;
import com.ccthub.userservice.model.User;
import com.ccthub.userservice.repository.UserRepository;
import com.ccthub.userservice.util.JwtTokenProvider;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${ai.default.model:claude-haiku-4.5}")
    private String aiDefaultModel;

    public RegisterResponse register(RegisterRequest request) throws Exception {
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new Exception("User already exists");
        }

        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus("ACTIVE");

        // 设置加密手机号
        user.setPhoneEncrypted(encryptPhone(request.getPhone()));

        // 设置默认昵称（用户+手机号后4位）
        String phoneSuffix = request.getPhone().length() >= 4
                ? request.getPhone().substring(request.getPhone().length() - 4)
                : request.getPhone();
        user.setNickname("用户" + phoneSuffix);

        // 设置注册时间
        user.setRegisterTime(LocalDateTime.now());

        // 设置最后登录时间
        user.setLastLoginTime(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(
                savedUser.getId().toString(),
                savedUser.getPhone());
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                savedUser.getId().toString(),
                savedUser.getPhone());

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getPhone(),
                accessToken,
                refreshToken,
                aiDefaultModel);
    }

    public RegisterResponse login(LoginRequest request) throws Exception {
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new Exception("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception("Invalid password");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId().toString(),
                user.getPhone());
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId().toString(),
                user.getPhone());

        return new RegisterResponse(
                user.getId(),
                user.getPhone(),
                accessToken,
                refreshToken,
                aiDefaultModel);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByPhone(String phone) {
        return userRepository.findByPhone(phone).orElse(null);
    }

    /**
     * 加密手机号（使用SHA-256）
     */
    private String encryptPhone(String phone) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedPhone = phone + "salt"; // 简单加盐，实际应使用配置
            byte[] hash = digest.digest(saltedPhone.getBytes(StandardCharsets.UTF_8));

            // 转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt phone", e);
        }
    }
}
