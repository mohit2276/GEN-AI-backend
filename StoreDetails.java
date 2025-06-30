package com.genai.codeiumapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_details")
public class StoreDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int storeId;

	@Column(name = "store_name")
	private String storeName;

	@Column(name = "street")
	private String street;

	@Column(name = "city")
	private String city;

	@Column(name = "country")
	private String country;

	@Column(name = "state")
	private String state;

	@Column(name = "zipcode")
	private String zipCode;
	
	private double latitude;
	
	private double longitude;



	public String getAddress() {
        return city + ", " + state + ", " + country;
    }
	
	public void setAddress(String string) {
		//This method is for setting address
	}
}

