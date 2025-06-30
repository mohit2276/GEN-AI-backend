package com.genai.codeiumapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
	private String name;
    private String email;
    private String storeName;
    private String address;
    private String KPI;
    private String totalChallenge;
    private String winningChallenge;
    private String enrolledChallenge;
    private String co2Savings;
    private String dollarSavings;
    private String wasteSavings;
}
