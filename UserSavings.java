package com.genai.codeiumapp.model;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
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
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserSavings {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;	
	private double dollarSavings;
	private double wasteSavings;
	private double co2Savings;
	private LocalDate savingsAddedOn;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_userId")
	private User user;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "challenge_id")
	private Challenge challenge;
	
}
