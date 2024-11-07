package com.learn.matchmaking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {

    private List<List<PlayerBasicDTO>> groups;
    private String message;
}
