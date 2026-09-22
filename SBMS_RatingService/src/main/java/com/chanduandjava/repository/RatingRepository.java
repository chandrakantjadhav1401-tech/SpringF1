package com.chanduandjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chanduandjava.entity.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, String> {

	//create the custom method
	List<Rating> findByUserId(String userId);
	
	List<Rating> findByHotelId(String hotelId);
}
