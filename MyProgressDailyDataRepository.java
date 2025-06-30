package com.genai.codeiumapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genai.codeiumapp.model.MyProgressDailyData;

public interface MyProgressDailyDataRepository extends JpaRepository<MyProgressDailyData,Long> {
	 void deleteByChallengeId(Long challengeId);

}
