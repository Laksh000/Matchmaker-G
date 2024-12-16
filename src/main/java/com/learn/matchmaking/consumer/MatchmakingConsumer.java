package com.learn.matchmaking.consumer;

import com.learn.matchmaking.dto.MatchRequest;
import com.learn.matchmaking.dto.MatchResponse;
import com.learn.matchmaking.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MatchmakingConsumer {

    private final MatchService matchService;

    @KafkaListener(topics = "matchmaking-requests", groupId = "match-request-consumer")
    public void processMatchRequest(@Payload MatchRequest matchRequest,
                                    @Header("username") String username
    ) {

        try {

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(
                    new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList())
            );
            SecurityContextHolder.setContext(securityContext);

            System.out.println("SecurityContext in consumer: " + SecurityContextHolder.getContext().getAuthentication());

            MatchResponse response = matchService.getGroupsFromPool(matchRequest);

            if (!response.getGroups().isEmpty()) {
                System.out.println("Matchmaking successful: " + response);
            } else {
                System.out.println("Matchmaking failed: " + response);
            }
        } finally {

            SecurityContextHolder.clearContext();
        }
    }
}
