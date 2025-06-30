package com.genai.codeiumapp.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.genai.codeiumapp.model.Company;
import com.genai.codeiumapp.model.User;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data

public class UserInfoUserDetails implements UserDetails {
	
	private String employeeId;
	private String password;
	private List<GrantedAuthority> authorities;


	public UserInfoUserDetails(User user, Company company) {
		if(user != null) {
			this.employeeId = user.getUserEmpId();
			this.password = user.getPassword();
			this.authorities = Arrays.stream(user.getRole().split(","))
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());
		} else if(company != null) {
			this.employeeId = company.getUserEmpId();
			this.password = company.getPassword();
			this.authorities = Arrays.stream(company.getRole().split(","))
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList());
		}
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return authorities;
	}

	@Override
	public String getPassword() {
		
		return password;
	}

	@Override
	public String getUsername() {
		return employeeId;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
