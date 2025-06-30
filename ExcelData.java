package com.genai.codeiumapp.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class ExcelData {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    private LocalDate uploadDate;
    private String market;
    private String storeName;
    private String storeId;
    private String managerName;
    private String emailId;
    private String address;
    private String masterFranchise;
    
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fileDetails_id")
    private FileDetails fileDetails;

   
}