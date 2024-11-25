package com.learn.matchmaking.controller;

import com.learn.matchmaking.dto.MatchRequest;
import com.learn.matchmaking.dto.MatchResponse;
import com.learn.matchmaking.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Matchmaking")
@RestController
@RequestMapping("match")
public class MatchController {

    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {

        this.matchService = matchService;
    }

    @Operation(summary = "Matchmaking players from the pool of players(i.e:isSearchingForMatch flag is true), here there's no need of player id's.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Matchmaking Successful",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MatchResponse.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Matchmaking Failed: \"The number of active players doesn't match the criteria for matchmaking\"",
                    content = {@Content}
            ),
            @ApiResponse(
                    responseCode = "403", description = "No Authorization",
                    content = {@Content}
            )
    })
    @PostMapping("pool")
    public ResponseEntity<MatchResponse> matchGroupFromPool(@RequestBody MatchRequest matchRequest) {

        MatchResponse response = matchService.getGroupsFromPool(matchRequest);

        if(!response.getGroups().isEmpty()) {

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Matchmaking players from the given player id's, here player id's are must")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Matchmaking Successful",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MatchResponse.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Matchmaking Failed: \"Player Id's are mandatory\"",
                    content = {@Content}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Matchmaking Failed: \"Player not found\"",
                    content = {@Content}
            ),
            @ApiResponse(
                    responseCode = "403", description = "No Authorization",
                    content = {@Content}
            )
    })
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
