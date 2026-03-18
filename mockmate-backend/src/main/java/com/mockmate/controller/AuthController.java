package com.mockmate.controller;

import com.mockmate.dto.LoginRequest;
import com.mockmate.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<LoginRequest.TokenResponse> register(@Valid @RequestBody LoginRequest.Register request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginRequest.TokenResponse> login(@Valid @RequestBody LoginRequest.Login request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
