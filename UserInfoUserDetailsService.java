package com.genai.codeiumapp.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.genai.codeiumapp.model.Company;
import com.genai.codeiumapp.model.User;
import com.genai.codeiumapp.repository.CompanyRepository;
import com.genai.codeiumapp.repository.UserRepository;

@Component
public class UserInfoUserDetailsService implements UserDetailsService {
	@Autowired
	 private UserRepository userRepository;
	@Autowired
	private CompanyRepository companyRepository;


	@Override
	public UserDetails loadUserByUsername(String employeeId) throws UsernameNotFoundException {
	    Optional<User> eOptional = userRepository.findByUserEmpId(employeeId);
	    if (eOptional.isPresent()) {
	        return eOptional.map(user -> new UserInfoUserDetails(user, null)).get();
	    }

	    Optional<Company> cOptional = companyRepository.findByUserEmpId(employeeId);
	    return cOptional.map(company -> new UserInfoUserDetails(null, company)).orElseThrow(() -> new UsernameNotFoundException("User not found with employee id: " + employeeId));
	}
}
