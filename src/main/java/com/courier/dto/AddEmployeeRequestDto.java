package com.courier.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddEmployeeRequestDto {
    @NotBlank public String name;
    public String address;
    @NotNull  public Long    phone;
    @NotNull  public Integer pincode;
    @NotBlank public String  username;
    @NotBlank public String  password;

    public String  getName()     { return name; }
    public String  getAddress()  { return address; }
    public Long    getPhone()    { return phone; }
    public Integer getPincode()  { return pincode; }
    public String  getUsername() { return username; }
    public String  getPassword() { return password; }
    public void setName(String n)       { name = n; }
    public void setAddress(String a)    { address = a; }
    public void setPhone(Long p)        { phone = p; }
    public void setPincode(Integer pin) { pincode = pin; }
    public void setUsername(String u)   { username = u; }
    public void setPassword(String pw)  { password = pw; }
}
