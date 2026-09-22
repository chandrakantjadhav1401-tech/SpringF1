package com.chanduandjava.service.Impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chanduandjava.entity.Hotel;
import com.chanduandjava.exceptions.ResourceNotFoundException;
import com.chanduandjava.repository.HotelRepository;
import com.chanduandjava.service.HotelService;

@Service
public class HotelServiceImpl implements HotelService {

	@Autowired
   private HotelRepository hotelRepository;
	
	@Override
	public Hotel saveHotel(Hotel hotel) {
		
		 String hotelId = UUID.randomUUID().toString();
		 hotel.setId(hotelId);
		 return hotelRepository.save(hotel);
	}

	@Override
	public List<Hotel> getAll() {
		
		return hotelRepository.findAll();
	}

	@Override
	public Hotel getById(String id) {
		// TODO Auto-generated method stub
		return hotelRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("hotel with given id not found ="+id));
	}

}
