package com.genai.codeiumapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllDetailsDTO {

	private String name;
	private String email;
	private String storeName;
	private String address;
	
	
	private double co2Savings;
	private double dollarSavings;
	private double wasteSavings;
	private long totalChallenge;
	private long winningChallenge;
	private long enrolledChallenge;
}
