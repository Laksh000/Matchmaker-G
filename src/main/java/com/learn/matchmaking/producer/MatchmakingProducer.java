package com.learn.matchmaking.producer;

import com.learn.matchmaking.dto.MatchRequest;
import com.learn.matchmaking.model.MatchRequestStatus;
import com.learn.matchmaking.repo.MatchRequestStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MatchmakingProducer {

    private final KafkaTemplate<String, MatchRequest> kafkaTemplate;
    private final MatchRequestStatusRepository statusRepository;

    public CompletableFuture<SendResult<String, MatchRequest>> sendMatchmakingRequest(MatchRequest request) {

        String trackingId = UUID.randomUUID().toString();

        MatchRequestStatus status = new MatchRequestStatus();
        status.setTrackingId(trackingId);
        status.setStatus("QUEUED");
        statusRepository.save(status);

        Message<MatchRequest> message = MessageBuilder.withPayload(request)
                .setHeader(KafkaHeaders.TOPIC, "matchmaking-requests")
                .setHeader("trackingId", trackingId)
                .build();
        CompletableFuture<SendResult<String, MatchRequest>> future = kafkaTemplate.send(message);

        future.thenAccept(
                result -> {
                    System.out.println("Message sent successfully to topic "+ result.getRecordMetadata().topic()
                            + " with offset " + result.getRecordMetadata().offset());
                }
        ).exceptionally(
                ex -> {
                    System.out.println("Error sending message to topic "+ ex.getMessage());
                    return null;
                }
        );

        return future;
    }
}
