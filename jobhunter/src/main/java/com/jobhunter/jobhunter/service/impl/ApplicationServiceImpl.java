package com.jobhunter.jobhunter.service.impl;

import com.jobhunter.jobhunter.entity.AppEnums;
import com.jobhunter.jobhunter.entity.Application;
import com.jobhunter.jobhunter.entity.Job;
import com.jobhunter.jobhunter.entity.User;
import com.jobhunter.jobhunter.repository.ApplicationRepository;
import com.jobhunter.jobhunter.repository.JobRepository;
import com.jobhunter.jobhunter.repository.UserRepository;
import com.jobhunter.jobhunter.service.ApplicationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    // Thư mục lưu CV — cấu hình trong application.properties
    @Value("${app.upload.cv-dir:uploads/cv}")
    private String cvUploadDir;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
                                  UserRepository userRepository,
                                  JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    public Application apply(Long userId, Long jobId,
                             MultipartFile cvFile, String coverLetter) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job không tồn tại"));

        // E3: Job đã đóng hoặc hết hạn
        if (job.getStatusJob() == AppEnums.JobStatus.CLOSED) {
            throw new IllegalStateException("Vị trí này đã đóng tuyển dụng");
        }
        if (job.isExpired()) {
            throw new IllegalStateException("Vị trí này đã hết hạn tuyển dụng");
        }

        // E2: Đã apply rồi
        if (applicationRepository.existsByUserIdAndJobId(userId, jobId)) {
            throw new IllegalStateException("Bạn đã ứng tuyển vị trí này");
        }

        // E1: Validate file
        String cvPath = validateAndSaveFile(cvFile, userId, jobId);

        // Tạo Application
        Application application = new Application();
        application.setUser(user);
        application.setJob(job);
        application.setCvFile(cvPath);
        application.setCoverLetter(coverLetter);
        application.setStatus(AppEnums.ApplicationStatus.PENDING);

        Application saved = applicationRepository.save(application);

        System.out.println("=================================");
        System.out.println("APPLICATION CREATED");
        System.out.println("User  : " + user.getEmail());
        System.out.println("Job   : " + job.getTitle());
        System.out.println("CV    : " + cvPath);
        System.out.println("=================================");

        return saved;
    }

    @Override
    public List<Application> getByUserId(Long userId) {
        return applicationRepository.findByUserId(userId);
    }

    // ─── Validate + lưu file CV ──────────────────────────────────
    private String validateAndSaveFile(MultipartFile file, Long userId, Long jobId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng upload file CV");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("File không hợp lệ — chỉ chấp nhận PDF");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File vượt quá 5MB");
        }

        try {
            // Dùng absolute path thay vì relative
            Path uploadPath = Paths.get(cvUploadDir).toAbsolutePath().normalize();

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = userId + "_" + jobId + "_" + System.currentTimeMillis() + ".pdf";
            Path filePath = uploadPath.resolve(fileName);

            // Dùng Files.copy thay vì transferTo — hoạt động tốt trên Windows
            Files.copy(file.getInputStream(), filePath);

            // Lưu relative path vào DB
            return "uploads/cv/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage());
        }
    }
}
