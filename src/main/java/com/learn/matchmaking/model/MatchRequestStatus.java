package com.learn.matchmaking.model;

import com.learn.matchmaking.dto.MatchRequestStatusDTO;
import com.learn.matchmaking.dto.MatchResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "MatchRequestStatus")
public class MatchRequestStatus {

    @Id
    private String trackingId;
    private String status;
    private MatchResponse matchResponse;

    public MatchRequestStatus(MatchRequestStatusDTO matchRequestStatusDTO) {

        this.trackingId = matchRequestStatusDTO.getTrackingId();
        this.status = matchRequestStatusDTO.getStatus();
        this.matchResponse = matchRequestStatusDTO.getMatchResponse();
    }
}
