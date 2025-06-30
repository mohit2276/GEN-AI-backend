package com.genai.codeiumapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardDTO {
	
	    private String storeName;
	    private double co2Savings;
	    private int weekNumber;
	    private int rank;
	    
	    public LeaderboardDTO(String storeName, Double co2Savings, Integer weekNumber) {
	        this.storeName = storeName;
	        this.co2Savings = co2Savings;
	        this.weekNumber = weekNumber;
	    }
}
