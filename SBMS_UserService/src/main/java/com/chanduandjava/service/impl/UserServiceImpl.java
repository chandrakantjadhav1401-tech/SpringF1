package com.chanduandjava.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.chanduandjava.entity.Hotel;
import com.chanduandjava.entity.Rating;
import com.chanduandjava.entity.User;
import com.chanduandjava.exception.ResourceNotFoundException;
import com.chanduandjava.extenral.services.HotelService;
import com.chanduandjava.extenral.services.RatingService;
import com.chanduandjava.repository.UserRepository;
import com.chanduandjava.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private HotelService hotelService;
    
    @Autowired
    private RatingService ratingService;

    private static final Logger logger =
            LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

	 

//    @Override
//    public User getUser(String userId) {
//
//        // Step 1: Find user from User Service database
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "User with given id not found on server = " + userId
//                ));
//
//        // Step 2: Call Rating Service to get ratings of this user
//        String ratingServiceUrl =
//                "http://RATING-SERVICE/rating/userId/" + user.getUserId();
//
//        ResponseEntity<List<Rating>> ratingResponse =
//                restTemplate.exchange(
//                        ratingServiceUrl,
//                        HttpMethod.GET,
//                        null,
//                        new ParameterizedTypeReference<List<Rating>>() {}
//                );
//
//        List<Rating> ratingsOfUser = ratingResponse.getBody();
//
//        logger.info("Ratings received from Rating Service: {}", ratingsOfUser);
//
//        // Step 3: For each rating, call Hotel Service
//        List<Rating> ratingList = ratingsOfUser.stream()
//                .map(rating -> {
//
//                    String hotelServiceUrl =
//                            "http://HOTEL-SERVICE/hotels/byId/"
//                                    + rating.getHotelId();
//
//                    ResponseEntity<Hotel> hotelResponse =
//                            restTemplate.getForEntity(
//                                    hotelServiceUrl,
//                                    Hotel.class
//                            );
//
//                    Hotel hotel = hotelResponse.getBody();
//
//                    logger.info(
//                            "Hotel Service response status: {}",
//                            hotelResponse.getStatusCode()
//                    );
//
//                    // Step 4: Attach hotel details to rating
//                    rating.setHotel(hotel);
//
//                    return rating;
//                })
//                .collect(Collectors.toList());
//
//        // Step 5: Attach ratings to user
//        user.setRatings(ratingList);
//
//        // Step 6: Return complete user
//        return user;
//    }
    
    
    /*
     * NEW IMPLEMENTATION USING FEIGN CLIENT
     */

    @Override
    public User getUser(String userId) {

        // Step 1: Find user from User Service database
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User with ID " + userId + " not found"));

        // Step 2: Get ratings from Rating Service using Feign
        List<Rating> ratingsOfUser =
                ratingService.getRatingsByUserId(user.getUserId());

        logger.info(
                "Ratings received from Rating Service: {}",
                ratingsOfUser
        );

        // Step 3: Get hotel details for every rating
        List<Rating> ratingList = ratingsOfUser.stream()
                .map(rating -> {

                    // Call Hotel Service using Feign
                    Hotel hotel =
                            hotelService.getHotel(rating.getHotelId());

                    logger.info(
                            "Hotel received for hotel ID {}: {}",
                            rating.getHotelId(),
                            hotel
                    );

                    // Step 4: Attach hotel details to rating
                    rating.setHotel(hotel);

                    return rating;
                })
                .collect(Collectors.toList());

        // Step 5: Attach ratings to user
        user.setRatings(ratingList);

        // Step 6: Return complete user
        return user;
    }
   
    
    
    
}
