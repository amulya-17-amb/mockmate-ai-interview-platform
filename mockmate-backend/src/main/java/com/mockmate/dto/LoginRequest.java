package com.mockmate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class LoginRequest {

    @Data
    public static class Register {
        @NotBlank private String fullName;
        @Email @NotBlank private String email;
        @NotBlank private String password;
    }

    @Data
    public static class Login {
        @Email @NotBlank private String email;
        @NotBlank private String password;
    }

    @Data
    public static class TokenResponse {
        private String token;
        private String email;
        private String fullName;
        private Long candidateId;

        public TokenResponse(String token, String email, String fullName, Long candidateId) {
            this.token = token;
            this.email = email;
            this.fullName = fullName;
            this.candidateId = candidateId;
        }
    }
}
