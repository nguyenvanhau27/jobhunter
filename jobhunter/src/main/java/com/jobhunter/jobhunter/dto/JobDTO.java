package com.jobhunter.jobhunter.dto;

import com.jobhunter.jobhunter.entity.AppEnums;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class JobDTO {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String description;
    private String requirements;

    @NotNull(message = "Lương tối thiểu không được để trống")
    @Min(value = 0, message = "Lương không hợp lệ")
    private Long minSalary;

    @NotNull(message = "Lương tối đa không được để trống")
    private Long maxSalary;

    @NotBlank(message = "Địa điểm không được để trống")
    private String location;

    @NotNull(message = "Vui lòng chọn loại công việc")
    private AppEnums.JobType jobType;

    @NotNull(message = "Vui lòng chọn cấp độ kinh nghiệm")
    private AppEnums.ExperienceLevel experienceLevel;

    @NotNull(message = "Vui lòng chọn công ty")
    private Long companyId;

    // Multi-select skills
    private List<Long> skillIds;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public Long getMinSalary() { return minSalary; }
    public void setMinSalary(Long minSalary) { this.minSalary = minSalary; }

    public Long getMaxSalary() { return maxSalary; }
    public void setMaxSalary(Long maxSalary) { this.maxSalary = maxSalary; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public AppEnums.JobType getJobType() { return jobType; }
    public void setJobType(AppEnums.JobType jobType) { this.jobType = jobType; }

    public AppEnums.ExperienceLevel getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(AppEnums.ExperienceLevel experienceLevel) { this.experienceLevel = experienceLevel; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public List<Long> getSkillIds() { return skillIds; }
    public void setSkillIds(List<Long> skillIds) { this.skillIds = skillIds; }
}
