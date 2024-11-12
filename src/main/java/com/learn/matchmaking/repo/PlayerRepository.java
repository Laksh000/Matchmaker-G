package com.learn.matchmaking.repo;

import com.learn.matchmaking.model.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends MongoRepository<Player, String> {

    Player findByName(String name);
    List<Player> findByIsSearchingForMatch(boolean searchingForMatch);
}
