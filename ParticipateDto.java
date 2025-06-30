package com.genai.codeiumapp.model;


import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipateDto {
    private Long id;
    private String challengeName;
    private String createdBy;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Long assignTo;
    private Long createdByUserId;
    private String challengeStatus;
    private String enrollmentStatus;

    
}