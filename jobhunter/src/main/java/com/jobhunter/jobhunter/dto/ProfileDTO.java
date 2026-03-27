package com.jobhunter.jobhunter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfileDTO {

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @Size(max = 15, message = "Số điện thoại không hợp lệ")
    private String phone;

    private String address;

    private String experience;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
}