package com.learn.matchmaking.repo;

import com.learn.matchmaking.model.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends MongoRepository<Player, String> {

    Player findByName(String name);
}
