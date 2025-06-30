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
public class MonthlyDollarSavingsDto implements SavingsDto{

	
	private double dollarSavings;
	private LocalDate monthStartDate;
	private LocalDate monthEndDate;
}
