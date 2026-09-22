package com.chanduandjava.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staffs")
public class Staff {
	
	
	@GetMapping
	public ResponseEntity<List<String>> names()
	{
		List<String> asList = Arrays.asList("ram","chandu","shyam","naru");
		
		return new ResponseEntity<List<String>>(asList,HttpStatus.OK);
	}

}
