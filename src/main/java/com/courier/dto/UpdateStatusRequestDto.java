package com.courier.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateStatusRequestDto {
    @NotBlank public String status;
    @NotBlank public String empUsername;
    public String getStatus()      { return status; }
    public String getEmpUsername() { return empUsername; }
    public void setStatus(String s)      { status = s; }
    public void setEmpUsername(String e) { empUsername = e; }
}
