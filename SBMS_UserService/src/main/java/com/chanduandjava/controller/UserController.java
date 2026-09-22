package com.chanduandjava.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chanduandjava.entity.User;
import com.chanduandjava.service.UserService;
import com.chanduandjava.service.impl.UserServiceImpl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired 
	private  UserService userService;
	
	private static final Logger logger =
            LoggerFactory.getLogger(UserServiceImpl.class);
	
	
	//create
	
	@PostMapping("/saveUser")
	public ResponseEntity<User> createUser(@RequestBody User user) {
		User user1 = userService.saveUser(user);
		
		return new  ResponseEntity<User>(user1,HttpStatus.CREATED);
	}
	
	int retrycount=1;
	
	//single use get
	@GetMapping("/user/{userId}")
//	@CircuitBreaker(name = "ratingHotelBreaker" ,fallbackMethod = "ratingHotelFallback")
//	@Retry(name = "ratingHotelService",fallbackMethod = "ratingHotelFallback")
	@RateLimiter(name = "userRateLimiter",fallbackMethod = "ratingHotelFallback")
	public ResponseEntity<User> getUserById(@PathVariable String userId)
	{
		logger.info("Retry count: {}",retrycount);
		retrycount++;
		User userDB = userService.getUser(userId);
		
		return new ResponseEntity<User>(userDB,HttpStatus.OK);
	}
	
	
	
	//creating fall back method for circuitbreker
	
	public ResponseEntity<User> ratingHotelFallback(String userId, Exception exception) {

	    logger.info("Fallback is executed because the service is down: {}",
	            exception.getMessage());
		
		
		
	    User user = User.builder()
	            .email("dummy@gmail.com")
	            .name("Dummy")
	            .about("This is a dummy user because the service is down")
	            .userId("12345")
	            .build();

	    return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	
	//all user get
	@GetMapping("/getAllUsers")
	public ResponseEntity<List<User>> getAllUsers()
	{
		List<User> allUser = userService.getAllUser();
		
		return new ResponseEntity<List<User>>(allUser,HttpStatus.OK);
	}
}
