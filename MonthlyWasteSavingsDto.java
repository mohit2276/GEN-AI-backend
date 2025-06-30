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
public class MonthlyWasteSavingsDto implements SavingsDto{

	
	private double wasteSavings;
	private LocalDate monthStartDate;
	private LocalDate monthEndDate;
}
