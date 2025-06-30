package com.genai.codeiumapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.genai.codeiumapp.model.UserSavings;

@Repository
public interface UserSavingsRepository extends JpaRepository<UserSavings, Long> {


	Optional<UserSavings> findByUserUserId(long savingsId);

	List<UserSavings> findAllByUserUserId(long savingsId);

	List<UserSavings> findAllByUserUserIdAndChallengeId(long userId, long challengeId);

	Optional<UserSavings> findByUserUserIdAndChallengeId(Long userId, long challengeId);

	void deleteAllByChallengeId(long challengeId);



	List<UserSavings> findByChallengeId(long challengeId);


}
