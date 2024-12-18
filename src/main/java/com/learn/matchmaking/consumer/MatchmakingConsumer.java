package com.learn.matchmaking.consumer;

import com.learn.matchmaking.dto.MatchRequest;
import com.learn.matchmaking.dto.MatchRequestStatusDTO;
import com.learn.matchmaking.dto.MatchResponse;
import com.learn.matchmaking.exception.InvalidTrackingIdException;
import com.learn.matchmaking.model.MatchRequestStatus;
import com.learn.matchmaking.repo.MatchRequestStatusRepository;
import com.learn.matchmaking.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MatchmakingConsumer {

    private final MatchService matchService;
    private final MatchRequestStatusRepository statusRepository;

    @KafkaListener(topics = "matchmaking-requests", groupId = "match-request-consumer")
    public void processMatchRequest(@Payload MatchRequest matchRequest,
                                    @Header("trackingId") String trackingId
    ) {
        MatchRequestStatus status = statusRepository.findById(trackingId).orElseThrow(
                () -> new InvalidTrackingIdException("Invalid trackingId: " + trackingId)
        );
        status.setStatus("PROCESSING");
        statusRepository.save(status);

        MatchResponse response = matchService.getGroupsFromPool(matchRequest);

        if (!response.getGroups().isEmpty()) {

            status.setStatus("COMPLETED");
            status.setMatchResponse(response);

        } else {
            status.setStatus("FAILED");
        }
        statusRepository.save(status);
        System.out.println("Processed match request with tracking ID: " + trackingId);
    }
}
