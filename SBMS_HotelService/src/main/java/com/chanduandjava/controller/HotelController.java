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

import com.chanduandjava.entity.Hotel;
import com.chanduandjava.service.HotelService;

@RestController
@RequestMapping("/hotels")
public class HotelController {
	
	@Autowired
	private  HotelService hotelService;
	
	//create
	@PostMapping("/saveHotel")
	public ResponseEntity<Hotel> saveHotel(@RequestBody Hotel hotel)
	{
		Hotel saveHotel = hotelService.saveHotel(hotel);
		
		return new ResponseEntity<Hotel>(saveHotel,HttpStatus.CREATED);
	}
	
	
	//get single
	@GetMapping("/byId/{id}")
	public ResponseEntity<Hotel> getByIdHotel(@PathVariable String id) {
		Hotel hotelbyId = hotelService.getById(id);
		
		return new ResponseEntity<Hotel>(hotelbyId,HttpStatus.OK);
	}
	
	//get all
	
	@GetMapping("/allHotels")
	public ResponseEntity<List<Hotel>> getAll()
	{
		List<Hotel> all = hotelService.getAll();
		
		return new ResponseEntity<List<Hotel>>(all,HttpStatus.OK);
	}

}
