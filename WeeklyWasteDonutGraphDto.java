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
public class WeeklyWasteDonutGraphDto implements SavingsDto{
	private double co2Savings;
	private LocalDate weekStartDate;
	private LocalDate weekEndDate;
}
