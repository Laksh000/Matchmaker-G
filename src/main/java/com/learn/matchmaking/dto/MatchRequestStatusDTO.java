package com.learn.matchmaking.dto;

import com.learn.matchmaking.model.MatchRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequestStatusDTO {

    private String trackingId;
    private String status;
    private MatchResponse matchResponse;

    public MatchRequestStatusDTO(MatchRequestStatus matchRequestStatus) {

        this.trackingId = matchRequestStatus.getTrackingId();
        this.status = matchRequestStatus.getStatus();
        this.matchResponse = matchRequestStatus.getMatchResponse();
    }
}
