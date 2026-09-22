package com.chanduandjava.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> notFoundHandler(ResourceNotFoundException exception)
	{
		Map map=new HashMap();
		map.put("message", exception.getMessage());
		map.put("success", false);
		map.put("status", HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<Map<String,Object>>(map,HttpStatus.NOT_FOUND);
		
	}
}
