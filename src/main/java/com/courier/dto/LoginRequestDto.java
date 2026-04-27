package com.courier.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// ─────────────────────────────────────────────────────────
//  LOGIN
// ─────────────────────────────────────────────────────────
public class LoginRequestDto {
    @NotBlank public String username;
    @NotBlank public String password;
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void setUsername(String u) { username = u; }
    public void setPassword(String p) { password = p; }
}
