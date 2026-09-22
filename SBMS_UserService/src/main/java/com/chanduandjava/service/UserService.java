package com.chanduandjava.service;

import java.util.List;

import com.chanduandjava.entity.User;

public interface UserService {

	User saveUser(User user);
	
	List<User> getAllUser();
	
	User getUser(String userId);
	
}
