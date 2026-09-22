package com.chanduandjava.extenral.services;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.chanduandjava.entity.Rating;

@FeignClient(name = "RATING-SERVICE")
public interface RatingService {

    @GetMapping("/rating/userId/{userId}")
    List<Rating> getRatingsByUserId(
            @PathVariable("userId") String userId);
}