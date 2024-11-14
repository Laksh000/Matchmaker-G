package com.learn.matchmaking.controller;

import com.learn.matchmaking.dto.MatchRequest;
import com.learn.matchmaking.dto.MatchResponse;
import com.learn.matchmaking.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("match")
public class MatchController {

    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {

        this.matchService = matchService;
    }

    @PostMapping("pool")
    public ResponseEntity<MatchResponse> matchGroupFromPool(@RequestBody MatchRequest matchRequest) {

        MatchResponse response = matchService.getGroupsFromPool(matchRequest);

        if(!response.getGroups().isEmpty()) {

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("custom")
    public ResponseEntity<MatchResponse> matchGroupFromGivenIds(@RequestBody MatchRequest matchRequest) {

        MatchResponse response = matchService.getGroupsFromCustomIds(matchRequest);

        if(!response.getGroups().isEmpty()) {

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
