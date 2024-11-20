package com.learn.matchmaking.repo;

import com.learn.matchmaking.model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<Users, String> {

    Optional<Users> findByUsername(String name);
}
