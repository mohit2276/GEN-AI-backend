package com.genai.codeiumapp.dto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UploadHistoryFileDto {
	
	private Long id;
	private Long fileId;
	private String fileName;
	private String fileStatus;
	
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "uploadHistoryDto_id")
	private UploadHistoryDto uploadHistoryDto;

}
