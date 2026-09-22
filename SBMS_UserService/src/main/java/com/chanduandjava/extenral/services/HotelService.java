package com.chanduandjava.extenral.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.chanduandjava.entity.Hotel;

@FeignClient(name = "HOTEL-SERVICE")
public interface HotelService {
	
	@GetMapping("/hotels/byId/{hotelId}")
	public Hotel getHotel(@PathVariable String hotelId);
	
	

}
