package com.jobhunter.jobhunter.service.impl;

import com.jobhunter.jobhunter.dto.CompanyDTO;
import com.jobhunter.jobhunter.entity.Company;
import com.jobhunter.jobhunter.repository.CompanyRepository;
import com.jobhunter.jobhunter.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private static final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    // Các đuôi file ảnh được chấp nhận
    private static final List<String> ALLOWED_EXTENSIONS = List.of("png", "jpg", "jpeg", "webp", "svg");
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

    private final CompanyRepository companyRepository;

    @Value("${app.upload.logo-dir:uploads/logo}")
    private String logoUploadDir;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    @Override
    public Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Công ty không tồn tại"));
    }

    @Override
    @Transactional
    public Company create(CompanyDTO dto) {
        if (companyRepository.existsByNameCompany(dto.getNameCompany())) {
            throw new IllegalArgumentException("Công ty đã tồn tại trong hệ thống");
        }

        Company company = new Company();
        setBasicFields(company, dto);

        // Lưu logo nếu có upload
        if (hasFile(dto.getLogoFile())) {
            String logoPath = saveLogo(dto.getLogoFile(), dto.getNameCompany());
            company.setImageUrl(logoPath);
        }

        Company saved = companyRepository.save(company);
        log.info("COMPANY CREATED | id={} | name={} | logo={}",
                saved.getId(), saved.getNameCompany(), saved.getImageUrl());
        return saved;
    }

    @Override
    @Transactional
    public Company update(Long id, CompanyDTO dto) {
        Company company = findById(id);

        if (companyRepository.existsByNameCompanyAndIdNot(dto.getNameCompany(), id)) {
            throw new IllegalArgumentException("Tên công ty đã tồn tại trong hệ thống");
        }

        setBasicFields(company, dto);

        if (hasFile(dto.getLogoFile())) {
            // Xoá logo cũ nếu có (tránh orphan file)
            deleteOldLogo(company.getImageUrl());

            String logoPath = saveLogo(dto.getLogoFile(), dto.getNameCompany());
            company.setImageUrl(logoPath);
        } else {
            // Không upload logo mới → giữ nguyên logo cũ từ currentLogoPath
            company.setImageUrl(dto.getCurrentLogoPath());
        }

        Company saved = companyRepository.save(company);
        log.info("COMPANY UPDATED | id={} | name={} | logo={}",
                saved.getId(), saved.getNameCompany(), saved.getImageUrl());
        return saved;
    }

    @Override
    public List<Company> findTopCompanies(int limit) {
        return companyRepository.findTopCompaniesByOpenJobs(PageRequest.of(0, limit));
    }

    @Override
    public Page<Company> searchCompanies(String keyword, int page, int pageSize) {
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        return companyRepository.searchByName(kw, PageRequest.of(page, pageSize));
    }

    @Override
    public long countOpenJobs(Long companyId) {
        return companyRepository.countOpenJobsByCompanyId(companyId);
    }

    // ── Logo upload helpers

    /**
     * Lưu file logo vào uploads/logo/.
     * Tên file: <company-name-slugified>.<ext>
     * VD: "Shopee Vietnam" → "shopee-vietnam.png"
     */
    private String saveLogo(MultipartFile file, String companyName) {
        validateLogoFile(file);

        try {
            Path uploadPath = Paths.get(logoUploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            String ext = getExtension(file.getOriginalFilename());
            String slug = slugify(companyName);
            String fileName = slug + "." + ext;

            // Nếu tên file đã tồn tại → thêm timestamp để tránh ghi đè nhầm
            Path dest = uploadPath.resolve(fileName);
            if (Files.exists(dest)) {
                fileName = slug + "_" + System.currentTimeMillis() + "." + ext;
                dest = uploadPath.resolve(fileName);
            }

            Files.copy(file.getInputStream(), dest);
            log.info("LOGO SAVED | file={}", fileName);

            return "/uploads/logo/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu logo: " + e.getMessage());
        }
    }

    private void validateLogoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File logo trống");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Logo không được vượt quá 2MB");
        }
        String ext = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException(
                    "Định dạng không hỗ trợ. Chỉ chấp nhận: PNG, JPG, JPEG, WEBP, SVG");
        }
    }

    /** Xoá file logo cũ khỏi disk khi update logo mới */
    private void deleteOldLogo(String oldLogoPath) {
        if (oldLogoPath == null || oldLogoPath.isBlank()) return;
        try {
            Path old = Paths.get(oldLogoPath).toAbsolutePath().normalize();
            if (Files.exists(old)) {
                Files.delete(old);
                log.info("OLD LOGO DELETED | path={}", oldLogoPath);
            }
        } catch (IOException e) {
            // Không throw — xoá thất bại không block cập nhật company
            log.warn("Không thể xoá logo cũ: {}", oldLogoPath);
        }
    }

    /** Kiểm tra file có được upload không */
    private boolean hasFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    /** Lấy đuôi file, lowercase */
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * Chuyển tên company thành slug cho filename.
     * "Shopee Vietnam" → "shopee-vietnam"
     * Bỏ ký tự đặc biệt, thay khoảng trắng bằng dấu gạch ngang.
     */
    private String slugify(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")   // bỏ ký tự đặc biệt
                .replaceAll("\\s+", "-")             // khoảng trắng → gạch ngang
                .replaceAll("-+", "-")               // nhiều gạch ngang → 1
                .replaceAll("^-|-$", "");            // bỏ gạch ngang đầu/cuối
    }

    private void setBasicFields(Company company, CompanyDTO dto) {
        company.setNameCompany(dto.getNameCompany());
        company.setDescription(dto.getDescription());
        company.setSize(dto.getSize());
        company.setLocation(dto.getLocation());
        company.setWebsite(dto.getWebsite());
    }
}