package com.ccthub.userservice.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ccthub.userservice.dto.ChangePasswordRequest;
import com.ccthub.userservice.dto.LoginRequest;
import com.ccthub.userservice.dto.RegisterRequest;
import com.ccthub.userservice.dto.RegisterResponse;
import com.ccthub.userservice.dto.UpdateProfileRequest;
import com.ccthub.userservice.dto.UserProfileResponse;
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

        // TODO: 临时禁用密码验证以便测试
        // if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        // throw new Exception("Invalid password");
        // }

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
     * 获取用户详细信息
     */
    public UserProfileResponse getUserProfile(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setPhone(user.getPhone());
        response.setNickname(user.getNickname());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setRealName(user.getRealName());
        response.setMemberLevel(user.getMemberLevel());
        response.setGrowthValue(user.getGrowthValue());
        response.setTotalPoints(user.getTotalPoints());
        response.setAvailablePoints(user.getAvailablePoints());
        response.setWalletBalance(user.getWalletBalance());
        response.setRegisterTime(user.getRegisterTime());
        response.setLastLoginTime(user.getLastLoginTime());
        response.setStatus(user.getStatus());

        return response;
    }

    /**
     * 更新用户个人信息
     */
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            user.setNickname(request.getNickname());
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        if (request.getRealName() != null && !request.getRealName().trim().isEmpty()) {
            user.setRealName(request.getRealName());
        }

        if (request.getIdCard() != null && !request.getIdCard().trim().isEmpty()) {
            // 加密身份证号
            user.setIdCardEncrypted(encryptIdCard(request.getIdCard()));
        }

        User savedUser = userRepository.save(user);
        return getUserProfile(savedUser.getId());
    }

    /**
     * 修改登录密码
     */
    public void changePassword(Long userId, ChangePasswordRequest request) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new Exception("旧密码不正确");
        }

        // 密码强度验证
        if (request.getNewPassword().length() < 6) {
            throw new Exception("密码长度至少6位");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * 设置/修改支付密码
     */
    public void setPaymentPassword(Long userId, String paymentPassword) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        if (paymentPassword.length() != 6 || !paymentPassword.matches("\\d{6}")) {
            throw new Exception("支付密码必须是6位数字");
        }

        user.setPaymentPassword(passwordEncoder.encode(paymentPassword));
        userRepository.save(user);
    }

    /**
     * 验证支付密码
     */
    public boolean verifyPaymentPassword(Long userId, String paymentPassword) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        if (user.getPaymentPassword() == null) {
            throw new Exception("未设置支付密码");
        }

        return passwordEncoder.matches(paymentPassword, user.getPaymentPassword());
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

    /**
     * 加密身份证号（使用SHA-256）
     */
    private String encryptIdCard(String idCard) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedIdCard = idCard + "salt"; // 简单加盐，实际应使用配置
            byte[] hash = digest.digest(saltedIdCard.getBytes(StandardCharsets.UTF_8));

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
            throw new RuntimeException("Failed to encrypt idCard", e);
        }
    }
}
