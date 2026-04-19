package com.jobhunter.jobhunter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO dùng cho admin update thông tin user.
 * Không expose password field hiện tại — admin chỉ set password mới (optional).
 */
public class AdminUserDTO {

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    private String phone;
    private String address;
    private String experience;

    /** Role name: "USER" hoặc "ADMIN" */
    @NotBlank(message = "Role không được để trống")
    private String roleName;

    /**
     * Mật khẩu mới — optional.
     * Nếu để trống → giữ nguyên mật khẩu cũ.
     * Nếu có → phải >= 6 ký tự.
     */
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    private String newPassword;

    public AdminUserDTO() {}

    public String getFullName()               { return fullName; }
    public void setFullName(String v)         { this.fullName = v; }
    public String getPhone()                  { return phone; }
    public void setPhone(String v)            { this.phone = v; }
    public String getAddress()                { return address; }
    public void setAddress(String v)          { this.address = v; }
    public String getExperience()             { return experience; }
    public void setExperience(String v)       { this.experience = v; }
    public String getRoleName()               { return roleName; }
    public void setRoleName(String v)         { this.roleName = v; }
    public String getNewPassword()            { return newPassword; }
    public void setNewPassword(String v)      { this.newPassword = v; }
}