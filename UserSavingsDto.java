package com.genai.codeiumapp.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserSavingsDto {

	private double dollarSavings;
	private double wasteSavings;
	private double co2Savings;
	private LocalDate date;
}
