package com.genai.codeiumapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapDto {
	private double latitude;
	private double longitude;
	private double carbonSavingPoints;
	private String storeName;
	private int myProgress;
	private String managerName;
	private String country;
	private String state;
	private String street;
	private String city;
	private String zipCode;
	
}
