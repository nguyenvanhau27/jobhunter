package com.jobhunter.jobhunter.service.impl;

import com.jobhunter.jobhunter.dto.CompanyDTO;
import com.jobhunter.jobhunter.entity.Company;
import com.jobhunter.jobhunter.repository.CompanyRepository;
import com.jobhunter.jobhunter.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

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
    public Company create(CompanyDTO dto) {
        if (companyRepository.existsByNameCompany(dto.getNameCompany())) {
            throw new IllegalArgumentException("Công ty đã tồn tại trong hệ thống");
        }
        Company company = new Company();
        setFields(company, dto);
        return companyRepository.save(company);
    }

    @Override
    public Company update(Long id, CompanyDTO dto) {
        Company company = findById(id);
        if (companyRepository.existsByNameCompanyAndIdNot(dto.getNameCompany(), id)) {
            throw new IllegalArgumentException("Tên công ty đã tồn tại trong hệ thống");
        }
        setFields(company, dto);
        return companyRepository.save(company);
    }

    private void setFields(Company company, CompanyDTO dto) {
        company.setNameCompany(dto.getNameCompany());
        company.setDescription(dto.getDescription());
        company.setSize(dto.getSize());
        company.setLocation(dto.getLocation());
        company.setWebsite(dto.getWebsite());
        company.setImageUrl(dto.getImageUrl());
    }

    @Override
    public List<Company> findTopCompanies(int limit) {
        return companyRepository.findTopCompaniesByOpenJobs(
                PageRequest.of(0, limit));
    }
}