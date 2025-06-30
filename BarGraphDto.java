package com.genai.codeiumapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BarGraphDto {

	private int weekNumber;
	private int storeId;
	private double amount;
	private double co2Savings;
	private double wasteSavings;
	private double dollarSavings;
}
