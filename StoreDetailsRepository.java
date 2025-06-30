package com.genai.codeiumapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.genai.codeiumapp.model.StoreDetails;

@Repository
public interface StoreDetailsRepository extends JpaRepository<StoreDetails, Integer> {

	
}
