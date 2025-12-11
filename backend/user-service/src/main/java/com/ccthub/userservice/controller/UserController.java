package com.ccthub.userservice.controller;

import com.ccthub.userservice.dto.RegisterRequest;
import com.ccthub.userservice.dto.RegisterResponse;
import com.ccthub.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Value("${AI_DEFAULT_MODEL:claude-haiku-4.5}")
    private String aiDefaultModel;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest req) {
        var user = userService.registerUser(req);
        var resp = new RegisterResponse(user.getId(), "ok", aiDefaultModel);
        return ResponseEntity.ok(resp);
    }
}
