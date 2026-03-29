package com.audit.backend.dto;

import com.audit.backend.entities.User;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String role;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole().name();
    }
}