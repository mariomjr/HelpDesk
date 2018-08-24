package com.mariojr.helpdeskws.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mariojr.helpdeskws.api.entity.User;

public interface UserRepository extends MongoRepository<User, String>{

	User findByEmail(String email);
	
}
