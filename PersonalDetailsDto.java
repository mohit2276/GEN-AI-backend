package com.genai.codeiumapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDetailsDto {
	
	
	private String employeeName;
	private String employeeEmail;
	private String employeeId;
	private int storeId;	
	private String location;
	private String role;
	private byte[] imageUrl;
}
