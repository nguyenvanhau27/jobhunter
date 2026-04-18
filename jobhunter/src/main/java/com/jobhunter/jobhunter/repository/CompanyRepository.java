package com.jobhunter.jobhunter.repository;

import com.jobhunter.jobhunter.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsByNameCompany(String nameCompany);

    boolean existsByNameCompanyAndIdNot(String nameCompany, Long id);

    Optional<Company> findByNameCompany(String nameCompany);

    @Query("SELECT c FROM Company c " +
            "JOIN Job j ON j.company.id = c.id " +
            "WHERE j.statusJob = 'OPEN' " +
            "GROUP BY c " +
            "ORDER BY COUNT(j) DESC")
    List<Company> findTopCompaniesByOpenJobs(Pageable pageable);


    // Search theo tên — dùng cho list company USER/GUEST
    // keyword null/blank → trả tất cả (tránh logic if/else ở service)
    @Query("SELECT c FROM Company c " +
            "WHERE (:keyword IS NULL OR LOWER(c.nameCompany) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY c.nameCompany ASC")
    Page<Company> searchByName(@Param("keyword") String keyword, Pageable pageable);

    // Đếm số job OPEN theo company — hiển thị trên card
    @Query("SELECT COUNT(j) FROM Job j " +
            "WHERE j.company.id = :companyId AND j.statusJob = 'OPEN'")
    long countOpenJobsByCompanyId(@Param("companyId") Long companyId);
}