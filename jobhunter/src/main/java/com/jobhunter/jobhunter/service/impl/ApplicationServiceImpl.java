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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    // The folder where CVs are saved — configured in application.properties
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

        // E3: Job đã close or expired
        if (job.getStatusJob() == AppEnums.JobStatus.CLOSED) {
            throw new IllegalStateException("Vị trí này đã đóng tuyển dụng");
        }
        if (job.isExpired()) {
            throw new IllegalStateException("Vị trí này đã hết hạn tuyển dụng");
        }

        // E2: applied
        if (applicationRepository.existsByUser_IdAndJob_Id(userId, jobId)) {
            throw new IllegalStateException("Bạn đã ứng tuyển vị trí này");
        }

        // E1: Validate file
        String cvPath = validateAndSaveFile(cvFile, userId, jobId);

        // create Application
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
        return applicationRepository.findByUser_Id(userId);
    }


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

            // Use Files.copy instead of transferTo — it works well on Windows
            Files.copy(file.getInputStream(), filePath);

            // Save relative path to DB
            return "uploads/cv/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage());
        }
    }

    @Override
    public List<Application> searchMyApplications(Long userId, String companyName,
                                                  int page, int pageSize) {
        List<Application> filtered = applyFilter(
                applicationRepository.findByUser_Id(userId), companyName);

        filtered.sort(Comparator.comparing(Application::getAppliedAt).reversed());

        int start = page * pageSize;
        if (start >= filtered.size()) return List.of();
        return filtered.subList(start, Math.min(start + pageSize, filtered.size()));
    }

    @Override
    public int countMyApplications(Long userId, String companyName) {
        return applyFilter(applicationRepository.findByUser_Id(userId), companyName).size();
    }

    // ── Filter theo tên công ty ──────────────────────────────────
    private List<Application> applyFilter(List<Application> all, String companyName) {
        if (companyName == null || companyName.isBlank()) return all;

        String keyword = companyName.toLowerCase().trim();
        return all.stream()
                .filter(app -> app.getJob() != null
                        && app.getJob().getCompany() != null
                        && app.getJob().getCompany().getNameCompany() != null
                        && app.getJob().getCompany().getNameCompany()
                        .toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    // ── Validate + save CV ───────────────────────────────────────
//    private String validateAndSaveFile(MultipartFile file, Long userId, Long jobId) {
//        if (file == null || file.isEmpty()) {
//            throw new IllegalArgumentException("Vui lòng upload file CV");
//        }
//        String originalName = file.getOriginalFilename();
//        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
//            throw new IllegalArgumentException("File không hợp lệ — chỉ chấp nhận PDF");
//        }
//        if (file.getSize() > 5 * 1024 * 1024) {
//            throw new IllegalArgumentException("File vượt quá 5MB");
//        }
//        try {
//            Path uploadPath = Paths.get(cvUploadDir).toAbsolutePath().normalize();
//            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
//
//            String fileName = userId + "_" + jobId + "_" + System.currentTimeMillis() + ".pdf";
//            Path filePath = uploadPath.resolve(fileName);
//            Files.copy(file.getInputStream(), filePath);
//
//            return "uploads/cv/" + fileName;
//        } catch (IOException e) {
//            throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage());
//        }
//    }
}
