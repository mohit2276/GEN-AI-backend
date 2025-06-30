package com.genai.codeiumapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genai.codeiumapp.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUserEmpId(String empId);

	Optional<User> findByUserEmpId(String userEmpId);

	Optional<User> findByEmail(String email);

	Optional<User> findByRole(String string);
	
	Optional<User> findByName(String name);



	List<User> findAllByCompanyCompanyId(Long companyId);

	boolean existsByUserEmpIdOrEmail(String empId, String email);


	Optional<User> findByStoreDetailsStoreId(int i);

}
