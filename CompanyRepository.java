package com.genai.codeiumapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.genai.codeiumapp.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>{

	boolean existsByUserEmpId(String empId);

	Optional<Company> findByUserEmpId(String username);


	Optional<Company> findByEmail(String email);


	boolean existsByUserEmpIdOrEmail(String empId, String email);


	
}
