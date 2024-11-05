package com.learn.matchmaking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchRequest {

    private List<String> playerIds; // Optional, used for custom ID-based matchmaking
    private Map<String, Object> targetAttributes; // Attributes for matching
    private Map<String, Double> attributeWeights; // Weights for each attribute
    private int groupSize; // Number of players per group
    private double matchThreshold; // Minimum score for a match
    private boolean matchTypeFair; //To be Matched fair or not

}
