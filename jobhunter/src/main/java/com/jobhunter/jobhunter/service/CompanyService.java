package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.dto.CompanyDTO;
import com.jobhunter.jobhunter.entity.Company;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CompanyService {

    List<Company> findAll();

    Company findById(Long id);

    Company create(CompanyDTO dto);

    Company update(Long id, CompanyDTO dto);

    // Top company: most job open
    List<Company> findTopCompanies(int limit);


    // List company USER/GUEST — search theo tên + phân trang
    Page<Company> searchCompanies(String keyword, int page, int pageSize);

    // Đếm job OPEN của 1 company — hiển thị trên card và detail page
    long countOpenJobs(Long companyId);
}
