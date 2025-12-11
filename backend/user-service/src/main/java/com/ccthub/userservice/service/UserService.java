package com.ccthub.userservice.service;

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
}
