package com.jobhunter.jobhunter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class CompanyDTO {

    @NotBlank(message = "Tên công ty không được để trống")
    @Size(max = 255, message = "Tên công ty không được vượt quá 255 ký tự")
    private String nameCompany;

    private String description;

    @Size(max = 50, message = "Quy mô không được quá 50 ký tự")
    private String size;

    @Size(max = 255, message = "Địa điểm không được quá 255 ký tự")
    private String location;

    @Size(max = 255, message = "Website không được quá 255 ký tự")
    private String website;

    /**
     * Optional: khi update có thể không chọn file mới → giữ logo cũ.
     */
    private MultipartFile logoFile;

    /**
     * Path logo hiện tại — truyền từ form ẩn khi update.
     * Service dùng để giữ lại logo cũ nếu không upload file mới.
     */
    private String currentLogoPath;

    public CompanyDTO() {}

    public String getNameCompany()                { return nameCompany; }
    public void setNameCompany(String v)          { this.nameCompany = v; }
    public String getDescription()                { return description; }
    public void setDescription(String v)          { this.description = v; }
    public String getSize()                       { return size; }
    public void setSize(String v)                 { this.size = v; }
    public String getLocation()                   { return location; }
    public void setLocation(String v)             { this.location = v; }
    public String getWebsite()                    { return website; }
    public void setWebsite(String v)              { this.website = v; }
    public MultipartFile getLogoFile()            { return logoFile; }
    public void setLogoFile(MultipartFile v)      { this.logoFile = v; }
    public String getCurrentLogoPath()            { return currentLogoPath; }
    public void setCurrentLogoPath(String v)      { this.currentLogoPath = v; }
}