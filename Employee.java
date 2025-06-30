package com.genai.codeiumapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Employee{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	 private Long Id;
	 private String employeeName;
	 private String empId;
	 private String empEmail;
	 private String empStoreName;
	 private String empState;
	 private String empCity;
	 private String empCountry;
	 private String empRegDate;
	 private String KPI;
	 private String totalChallenge;
	 private String winChallenge;
	 private String enrolledChallenge;
	 private String carbonSaving;
	 private String dollarSaving;
	 private String wasteSavings;
	 
	  @ManyToOne
	  @JoinColumn(name = "user_id", referencedColumnName = "id")
	  private User user;
	  
	  @ManyToOne
	    @JoinColumn(name = "challenge_id") // Assuming this is the foreign key column in the Employee table
	    private Challenge challenge;

	  
	  public String getAddress() {
	        return empCity + ", " + empState + ", " + empCountry;
	    }

	public void setAddress(String string) {
		// This method is intentionally left empty because the address setting functionality
	    // is not applicable to this class. The address management is handled by a different
	    // component in the system. Modifying or removing this method may cause unexpected
	    // behavior elsewhere in the codebase.
	}
	 
}
