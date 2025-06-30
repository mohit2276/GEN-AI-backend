package com.genai.codeiumapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.genai.codeiumapp.model.Employee;

public interface EmployeeRepository  extends JpaRepository<Employee,Long>{
	
	@Query("SELECT e FROM Employee e JOIN e.user u JOIN e.challenge c WHERE u.id = :userId AND c.id = :challengeId")
    List<Employee> findByUserIdAndChallengeId(Long userId, Long challengeId);

	Optional<Employee> findByEmpId(String employeeId);
	
	 void deleteByChallengeId(Long challengeId);

}
