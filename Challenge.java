package com.genai.codeiumapp.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Challenge {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "challenge_id")
    private Long id;
    private String challengeName;
    private String description;
    private String evaluationKpi;
    private String eligibility;
    private String createdBy;
    private String dataRequired;
    private String winner;
    private String firstRunnerUp;
    private String secondRunnerUp;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate winnerAnnouncementDate;
    private String status;
    private Long createdByUserId;
    
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeAttribute> challengeAttributes;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    
    @JsonIgnore
    @ManyToOne
	  @JoinColumn(name = "user_id", referencedColumnName = "id")
	  private User user;

   
}