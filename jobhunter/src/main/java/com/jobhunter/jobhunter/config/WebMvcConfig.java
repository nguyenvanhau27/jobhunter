package com.jobhunter.jobhunter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.cv-dir:uploads/cv}")
    private String cvUploadDir;

    @Value("${app.upload.logo-dir:uploads/logo}")
    private String logoUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve CV PDF — /uploads/cv/**
        String cvPath = Paths.get(cvUploadDir).toAbsolutePath().normalize().toString();
        registry.addResourceHandler("/uploads/cv/**")
                .addResourceLocations("file:" + cvPath + "/");

        // Serve Company Logo — /uploads/logo/**
        String logoPath = Paths.get(logoUploadDir).toAbsolutePath().normalize().toString();
        registry.addResourceHandler("/uploads/logo/**")
                .addResourceLocations("file:" + logoPath + "/");
    }
}