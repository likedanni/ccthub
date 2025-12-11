package com.ccthub.userservice.service;

import com.ccthub.userservice.dto.RegisterRequest;
import com.ccthub.userservice.model.User;
import com.ccthub.userservice.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(RegisterRequest req) {
        userRepository.findByPhone(req.phone).ifPresent(u -> {
            throw new IllegalArgumentException("phone already registered");
        });

        var user = new User();
        user.setPhone(req.phone);
        user.setPasswordHash(passwordEncoder.encode(req.password));
        return userRepository.save(user);
    }
}
