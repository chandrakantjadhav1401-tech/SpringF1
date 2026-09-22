package com.chanduandjava.exception;

public class ResourceNotFoundException extends RuntimeException {
	
	public ResourceNotFoundException() {
		
		super("Resources not found on server !!");
 	}
	
	
public ResourceNotFoundException(String msg) {
		
		super(msg);
 	}
}
