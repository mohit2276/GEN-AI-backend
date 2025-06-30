package com.genai.codeiumapp.exceptions;

@SuppressWarnings("serial")
public class EmployeeIdExistsException extends RuntimeException {

	 public EmployeeIdExistsException(String message) {
	        super(message);
	    }
}
