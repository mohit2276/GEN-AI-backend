package com.genai.codeiumapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.genai.codeiumapp.model.Challenge;
import com.genai.codeiumapp.model.ChallengeEnrollment;
import com.genai.codeiumapp.model.User;

public interface ChallengeEnrollmentRepo extends JpaRepository<ChallengeEnrollment, Long> {

	List<ChallengeEnrollment> findByUser(User user);

	ChallengeEnrollment findByChallenge(Challenge challenge);

	List<ChallengeEnrollment> findAllByUserUserIdAndEnrollmentStatus(long userId, String enrollmentStatus);

	List<ChallengeEnrollment> findAllByUserUserIdAndChallengeStatus(long userId, String challengeStatus);

	List<ChallengeEnrollment> findAllByUserUserIdAndChallengeStatusAndMyProgress(long userId, String string, int i);

	List<ChallengeEnrollment> findAllByUserUserId(long userId);

	@Query("SELECT COUNT(DISTINCT e) FROM ChallengeEnrollment e WHERE e.user = :user")
	long countByUser(@Param("user") User user);

	List<ChallengeEnrollment> findByUserUserIdAndEnrollmentStatus(Long userId, String enrollmentStatus);

	Optional<ChallengeEnrollment> findByUserUserIdAndChallengeIdAndEnrollmentStatus(Long userId, long challengeId, String enrolled);

	Optional<ChallengeEnrollment> findByChallengeAndUser(Challenge challenge, User user);

	Optional<ChallengeEnrollment> findByUserUserIdAndChallengeId(Long userId, long challengeId);
	
	 void deleteByChallengeId(Long challengeId);
}
