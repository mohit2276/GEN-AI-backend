package com.genai.codeiumapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
    private Long id;

    @Column(name = "challenge_name")
    private String challengeName;

    @Column(name = "date")
    private String launchDate;

    @Column(name = "time")
    private String launchTime;
    
    @Column(name="user_name")
    private String name;
    
    @Column(name="emp_id")
    private String userEmpId;
    
    @Column(name="register_date")
    private String registrationDate;
    
    @Column(name="register_time")
    private String registrationTime;
    
    @Column(name="enroll_date")
    private String enrollDate;
    
    @Column(name="enroll_time")
    private String enrollTime;
    
    @Column(name = "is_read")
    private Boolean isRead;
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    

 

}
