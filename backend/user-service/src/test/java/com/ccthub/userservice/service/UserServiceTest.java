package com.ccthub.userservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.ccthub.userservice.dto.RegisterRequest;
import com.ccthub.userservice.model.User;
import com.ccthub.userservice.repository.UserRepository;
import com.ccthub.userservice.util.JwtTokenProvider;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        jwtTokenProvider = Mockito.mock(JwtTokenProvider.class);

        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(userService, "jwtTokenProvider", jwtTokenProvider);
        ReflectionTestUtils.setField(userService, "aiDefaultModel", "claude-haiku-4.5");
    }

    @Test
    void registerSuccess() throws Exception {
        when(userRepository.findByPhone("13800000000")).thenReturn(Optional.empty());
        when(jwtTokenProvider.generateAccessToken("1", "13800000000")).thenReturn("access_token");
        when(jwtTokenProvider.generateRefreshToken("1", "13800000000")).thenReturn("refresh_token");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            var u = inv.getArgument(0, User.class);
            u.setId(1L);
            return u;
        });

        var req = new RegisterRequest();
        req.setPhone("13800000000");
        req.setPassword("p@ssw0rd");

        var resp = userService.register(req);
        assertThat(resp.getId()).isEqualTo(1L);
        assertThat(resp.getPhone()).isEqualTo("13800000000");
        assertThat(resp.getAccessToken()).isEqualTo("access_token");
        assertThat(resp.getAiDefaultModel()).isEqualTo("claude-haiku-4.5");
    }

    @Test
    void registerDuplicatePhone() {
        var existing = new User();
        existing.setId(2L);
        existing.setPhone("13800000000");
        when(userRepository.findByPhone("13800000000")).thenReturn(Optional.of(existing));

        var req = new RegisterRequest();
        req.setPhone("13800000000");
        req.setPassword("p@ssw0rd");

        assertThatThrownBy(() -> userService.register(req)).isInstanceOf(Exception.class);
    }
}
