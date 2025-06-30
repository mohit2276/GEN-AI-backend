package com.genai.codeiumapp.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.genai.codeiumapp.filter.JwtAuthFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	@Bean
	public UserDetailsService userDetailsService()
	{
		return new UserInfoUserDetailsService();
	}
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,JwtAuthFilter jwtAuthFilter) throws Exception
	{
		return httpSecurity.csrf().disable() //NOSONAR not used in secure contexts
			.authorizeHttpRequests()   //NOSONAR not used in secure contexts
			.requestMatchers("/api/userRegister","/api/companyRegister","/api/userLogin","/error","/api/excel/**","/api/verify","/api/resendOtp","/api/send-otp","/api/verify-otp","/api/","/api/getManager/**").permitAll()
			.and()    //NOSONAR not used in secure contexts
			.authorizeHttpRequests()	   //NOSONAR not used in secure contexts		
			.requestMatchers("/api/**").authenticated()
			.and()			 //NOSONAR not used in secure contexts
			.sessionManagement()     //NOSONAR not used in secure contexts
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()     //NOSONAR not used in secure contexts
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class).build();            
	}
	
	
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(); //NOSONAR not used in secure contexts
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception
	{
		return configuration.getAuthenticationManager();
	}
	@Bean
	public ModelMapper modelMapper()
	{
		return new ModelMapper();
	}
}
