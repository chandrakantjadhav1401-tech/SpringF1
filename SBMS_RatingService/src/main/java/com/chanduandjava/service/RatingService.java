package com.chanduandjava.service;

import java.util.List;

import com.chanduandjava.entity.Rating;

public interface RatingService {

	
	//create 
	
	Rating create(Rating rating);
	
	
	
	
	//get all Rating
	List<Rating> getAllRating();
	
	//get all by  userId
	List<Rating> getRatingByUserId(String userId);
	
	
	//get all by hotel
	List<Rating> getRatingByHotelId(String hotelId);
}
