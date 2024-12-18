package com.learn.matchmaking.repo;

import com.learn.matchmaking.model.MatchRequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRequestStatusRepository extends MongoRepository<MatchRequestStatus, String> {
}
