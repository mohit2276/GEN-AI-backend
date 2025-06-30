package com.genai.codeiumapp.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long userId;

	@Column(name = "user_name")
	private String name;

	@Column(name = "employee_id")
	private String userEmpId;

	@Column(name = "email")
	private String email;

	@Column(name = "date_of_birth")
	private LocalDate birthDate;

	@Column(name = "gender")
	private String gender;

	@Column(name = "password")
	private String password;

	private boolean verified;

	private LocalDate registrationDate;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Employee> employees;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "store_details_id")
	private StoreDetails storeDetails;
	
	@ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
	
	@OneToMany
	@JoinColumn(name = "user_id")
	private List<Challenge>challenges;
	
	private String role;
	
	private long otp;
	private boolean otpVerified;
	
	@Lob
    @Column(name = "image_url", columnDefinition = "LONGBLOB")
    private byte[] imageUrl;
	
	 public boolean isOtpVerified() {
	        return otpVerified;
	    }
	public void setOtpVerified(boolean otpVerified) {
	    this.otpVerified = otpVerified;
	}
	
	

}
