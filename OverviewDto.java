package com.genai.codeiumapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OverviewDto {
	
 private String name;
 private String employeeId;
 private String kpi;
 private String carbonSavingPoint="23.5";
 
}
