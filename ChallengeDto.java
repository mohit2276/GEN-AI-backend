package com.genai.codeiumapp.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDto {
    private Long id;
    private String challengeName;
    private String createdBy;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Long createdByUserId;

    
}