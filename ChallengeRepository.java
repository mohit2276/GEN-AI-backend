package com.genai.codeiumapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.genai.codeiumapp.model.Challenge;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge,Long> {

	List<Challenge> findByCreatedBy(String createdBy);
	
	List<Challenge> findByCreatedByUserId(Long createdByUserId);
	
	List<Challenge> findByStatusAndCreatedByUserId(String status, Long createdByUserId);
	
	List<Challenge> findAllByCreatedByUserId(long userId);

	List<Challenge> findAllByUserUserId(Long userId);

	List<Challenge> findAllByStatusAndUserUserId(String required, Long userId);

	List<Challenge> findAllByCompanyCompanyId(Long companyId);

	List<Challenge> findAllByStatusAndCompanyCompanyId(String required, Long companyId);

	void deleteById(long challengeId);

}
