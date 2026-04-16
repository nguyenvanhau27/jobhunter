package com.jobhunter.jobhunter.repository;

import com.jobhunter.jobhunter.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}