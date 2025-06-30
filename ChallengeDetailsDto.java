package com.genai.codeiumapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDetailsDto {

	private int ownedChallenges;
	private int ongoingChallenges;
	private int completedChallenges;
}
