package com.chanduandjava.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chanduandjava.entity.Rating;
import com.chanduandjava.service.RatingService;

@RestController
@RequestMapping("/rating")
public class RatingController {

	@Autowired
	private RatingService ratingService;
	
	
	
	@PostMapping("/saveRating")
	public ResponseEntity<Rating> createRating(@RequestBody Rating rating)
	{
		
	Rating rating2 = ratingService.create(rating);	
	
	return new ResponseEntity<Rating>(rating2,HttpStatus.CREATED);
	}
	
	
	@GetMapping("/getAll")
	public ResponseEntity<List<Rating>>  getRatings()
	{
		List<Rating> allRating = ratingService.getAllRating();
		
		return new ResponseEntity<List<Rating>>(allRating,HttpStatus.OK);
	}
	
	@GetMapping("/userId/{userId}")
	public ResponseEntity<List<Rating>> getRatingByUserId(@PathVariable String userId)
	{
		List<Rating> ratingByUserId = ratingService.getRatingByUserId(userId);
		
		return new ResponseEntity<List<Rating>>(ratingByUserId,HttpStatus.OK);
	}
	
	
	@GetMapping("/hotelId/{hotelId}")
	public ResponseEntity<List<Rating>> getRatingByHotelId(@PathVariable String hotelId)
	{
		List<Rating> ratingByHotelId = ratingService.getRatingByHotelId(hotelId);
		
		return new ResponseEntity<List<Rating>>(ratingByHotelId,HttpStatus.OK);
	}
	
	
	
	
	
}
