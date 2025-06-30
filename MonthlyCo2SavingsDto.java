package com.genai.codeiumapp.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyCo2SavingsDto implements SavingsDto{
	
	private double co2Savings;
	private LocalDate monthStartDate;
	private LocalDate monthEndDate;

}
