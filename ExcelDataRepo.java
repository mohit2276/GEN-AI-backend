package com.genai.codeiumapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.genai.codeiumapp.model.ExcelData;
import com.genai.codeiumapp.model.FileDetails;


@Repository
public interface ExcelDataRepo extends JpaRepository<ExcelData, Long> {
	
   List<ExcelData> findByFileDetails(FileDetails fileDetails);
}
