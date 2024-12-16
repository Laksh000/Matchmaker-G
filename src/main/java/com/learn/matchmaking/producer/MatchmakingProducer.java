package com.learn.matchmaking.producer;

import com.learn.matchmaking.dto.MatchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MatchmakingProducer {

    private final KafkaTemplate<String, MatchRequest> kafkaTemplate;

    public CompletableFuture<SendResult<String, MatchRequest>> sendMatchmakingRequest(MatchRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth!=null?auth.getName():"anonymous";

        Message<MatchRequest> message = MessageBuilder.withPayload(request)
                .setHeader(KafkaHeaders.TOPIC, "matchmaking-requests")
                .setHeader("username", username)
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
