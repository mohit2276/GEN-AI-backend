package com.genai.codeiumapp.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyProgressCompanyDetailsDto {

	private String storeName;
	private String storeStreet;
	private String storeCity;
	private String storeState;
	private String storeCountry;
	private String storeZipCode;
	private int myProgress;
	private LocalDate challengeEndingDate;
}
