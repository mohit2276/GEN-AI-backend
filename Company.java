package com.genai.codeiumapp.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long companyId;

	@Column(name = "company_name")
	private String companyName;

	@Column(name = "employee_id")
	private String userEmpId;

	@Column(name = "email")
	private String email;

	

	@Column(name = "password")
	private String password;


	private LocalDate registrationDate;

	@OneToMany
	@JoinColumn(name = "company_id")
	private List<User> users;
	
	
	@OneToMany
	@JoinColumn(name = "company_id")
	private List<Notification> notifications;
	
	@OneToMany
	@JoinColumn(name = "company_id")
	private List<Challenge>challenges;
	
	private String role;
	
	@Lob
    @Column(name = "image_url", columnDefinition = "LONGBLOB")
    private byte[] imageUrl;
	
	
}
