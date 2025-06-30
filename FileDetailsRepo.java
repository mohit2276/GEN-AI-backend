package com.genai.codeiumapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.genai.codeiumapp.model.FileDetails;
import java.util.List;


@Repository
public interface FileDetailsRepo extends JpaRepository<FileDetails, Long> {
	
	 List<FileDetails> findByUploader(String uploader);

}
