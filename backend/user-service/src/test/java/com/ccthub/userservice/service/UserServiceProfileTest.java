package com.ccthub.userservice.service;

import com.ccthub.userservice.dto.ChangePasswordRequest;
import com.ccthub.userservice.dto.UpdateProfileRequest;
import com.ccthub.userservice.dto.UserProfileResponse;
import com.ccthub.userservice.model.User;
import com.ccthub.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceProfileTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testUser = new User();
        testUser.setId(10L);
        testUser.setPhone("13800138000");
        testUser.setPhoneEncrypted("encrypted_phone");
        testUser.setNickname("测试用户");
        testUser.setMemberLevel(1);
        testUser.setWalletBalance(new BigDecimal("100.00"));
        testUser.setAvailablePoints(50);
        testUser.setPassword("$2a$10$hashedPassword");
    }

    @Test
    void testGetUserProfile_Success() throws Exception {
        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));

        UserProfileResponse response = userService.getUserProfile(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("13800138000", response.getPhone());
        assertEquals("测试用户", response.getNickname());
        assertEquals(1, response.getMemberLevel());
        assertEquals(new BigDecimal("100.00"), response.getWalletBalance());
        assertEquals(50, response.getAvailablePoints());
        
        verify(userRepository, times(1)).findById(10L);
    }

    @Test
    void testGetUserProfile_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            userService.getUserProfile(999L);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdateProfile_Success() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNickname("新昵称");

        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserProfileResponse response = userService.updateProfile(10L, request);

        assertNotNull(response);
        assertEquals("新昵称", testUser.getNickname());
        
        verify(userRepository, times(2)).findById(10L); // updateProfile calls getUserProfile internally
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testChangePassword_Success() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword123");

        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "$2a$10$hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("$2a$10$newHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.changePassword(10L, request);

        assertEquals("$2a$10$newHashedPassword", testUser.getPassword());
        verify(userRepository, times(1)).findById(10L);
        verify(passwordEncoder, times(1)).matches("oldPassword", "$2a$10$hashedPassword");
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testChangePassword_WrongOldPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrongPassword");
        request.setNewPassword("newPassword123");

        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

        Exception exception = assertThrows(Exception.class, () -> {
            userService.changePassword(10L, request);
        });

        assertEquals("旧密码不正确", exception.getMessage());
        verify(userRepository, times(1)).findById(10L);
        verify(passwordEncoder, times(1)).matches("wrongPassword", testUser.getPassword());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSetPaymentPassword_Success() throws Exception {
        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("888888")).thenReturn("$2a$10$paymentHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.setPaymentPassword(10L, "888888");

        assertEquals("$2a$10$paymentHashedPassword", testUser.getPaymentPassword());
        verify(userRepository, times(1)).findById(10L);
        verify(passwordEncoder, times(1)).encode("888888");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testVerifyPaymentPassword_Success() throws Exception {
        testUser.setPaymentPassword("$2a$10$paymentHashedPassword");
        
        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("888888", testUser.getPaymentPassword())).thenReturn(true);

        boolean result = userService.verifyPaymentPassword(10L, "888888");

        assertTrue(result);
        verify(userRepository, times(1)).findById(10L);
        verify(passwordEncoder, times(1)).matches("888888", testUser.getPaymentPassword());
    }

    @Test
    void testVerifyPaymentPassword_WrongPassword() throws Exception {
        testUser.setPaymentPassword("$2a$10$paymentHashedPassword");
        
        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("123456", testUser.getPaymentPassword())).thenReturn(false);

        boolean result = userService.verifyPaymentPassword(10L, "123456");

        assertFalse(result);
        verify(userRepository, times(1)).findById(10L);
        verify(passwordEncoder, times(1)).matches("123456", testUser.getPaymentPassword());
    }

    @Test
    void testVerifyPaymentPassword_NotSet() throws Exception {
        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));

        Exception exception = assertThrows(Exception.class, () -> {
            userService.verifyPaymentPassword(10L, "888888");
        });

        assertEquals("未设置支付密码", exception.getMessage());
        verify(userRepository, times(1)).findById(10L);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}
