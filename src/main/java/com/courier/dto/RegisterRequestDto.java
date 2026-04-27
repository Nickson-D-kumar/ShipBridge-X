package com.courier.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RegisterRequestDto {
    @NotBlank(message = "Username is required")   public String username;
    @NotBlank(message = "Password is required")   public String password;
    @NotBlank(message = "Name is required")       public String name;
    public String address;
    @NotNull(message = "Phone is required")
    @Positive(message = "Phone must be a positive number") public Long phone;

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName()     { return name; }
    public String getAddress()  { return address; }
    public Long   getPhone()    { return phone; }
    public void setUsername(String u) { username = u; }
    public void setPassword(String p) { password = p; }
    public void setName(String n)     { name = n; }
    public void setAddress(String a)  { address = a; }
    public void setPhone(Long ph)     { phone = ph; }
}
