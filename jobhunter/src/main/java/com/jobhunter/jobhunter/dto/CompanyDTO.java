package com.jobhunter.jobhunter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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

    @Size(max = 500, message = "Image URL không được quá 500 ký tự")
    private String imageUrl;

    public CompanyDTO() {
    }

    // GETTER + SETTER
    public String getNameCompany() {
        return nameCompany;
    }

    public void setNameCompany(String nameCompany) {
        this.nameCompany = nameCompany;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
