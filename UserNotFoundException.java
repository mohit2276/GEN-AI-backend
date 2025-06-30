package com.genai.codeiumapp.exceptions;

@SuppressWarnings("serial")
public class UserNotFoundException extends RuntimeException {

	 public UserNotFoundException(String message) {
	        super(message);
	    }
}
