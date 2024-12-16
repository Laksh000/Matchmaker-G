package com.learn.matchmaking.controller;

import com.learn.matchmaking.dto.MatchRequest;
import com.learn.matchmaking.dto.MatchResponse;
import com.learn.matchmaking.producer.MatchmakingProducer;
import com.learn.matchmaking.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@Tag(name = "Matchmaking")
@RestController
@RequestMapping("match")
@RequiredArgsConstructor
@Slf4j
public class MatchController {

    private final MatchService matchService;
    private final MatchmakingProducer producer;

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
    public CompletableFuture<ResponseEntity<String>> matchGroupFromPool(@RequestBody MatchRequest matchRequest) {
        System.out.println("SecurityContext at Controller: " + SecurityContextHolder.getContext().getAuthentication());
        return  producer.sendMatchmakingRequest(matchRequest)
                    .thenApply(
                            result -> ResponseEntity.status(HttpStatus.OK).body("Match request received and queued for processing.")
                    ).exceptionally(
                            ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage())
                    );
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
