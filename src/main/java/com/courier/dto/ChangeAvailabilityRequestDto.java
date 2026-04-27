package com.courier.dto;

import jakarta.validation.constraints.NotBlank;

public class ChangeAvailabilityRequestDto {
    @NotBlank public String availability;  // FREE | ON_LEAVE
    @NotBlank public String empUsername;
    public String getAvailability() { return availability; }
    public String getEmpUsername()  { return empUsername; }
    public void setAvailability(String a) { availability = a; }
    public void setEmpUsername(String e)  { empUsername = e; }
}
