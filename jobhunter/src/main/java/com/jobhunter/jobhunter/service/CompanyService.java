package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.dto.CompanyDTO;
import com.jobhunter.jobhunter.entity.Company;

import java.util.List;

public interface CompanyService {

    List<Company> findAll();

    Company findById(Long id);

    Company create(CompanyDTO dto);

    Company update(Long id, CompanyDTO dto);

    // Top company: nhiều job OPEN nhất
    List<Company> findTopCompanies(int limit);
}
