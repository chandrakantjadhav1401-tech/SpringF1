package com.chanduandjava.service;

import java.util.List;

import com.chanduandjava.entity.Hotel;

public interface HotelService {

	
	//create
	Hotel saveHotel(Hotel hotel);
	
	//getAll
	List<Hotel> getAll();
	
	// get single
	Hotel getById(String id);
	
	
}
