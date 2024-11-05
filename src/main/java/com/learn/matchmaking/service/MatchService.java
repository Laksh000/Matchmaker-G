package com.learn.matchmaking.service;

import com.learn.matchmaking.dto.MatchRequest;
import com.learn.matchmaking.dto.MatchResponse;
import com.learn.matchmaking.dto.PlayerBasicDTO;
import com.learn.matchmaking.model.Player;
import com.learn.matchmaking.repo.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private final PlayerRepository playerRepo;

    @Autowired
    public MatchService(PlayerRepository playerRepo) {

        this.playerRepo = playerRepo;
    }

    public MatchResponse getGroupsFromPool(MatchRequest matchRequest) {

        List<PlayerBasicDTO> searchingPlayers = playerRepo.findByIsSearchingForMatch(true)
                .stream().map(PlayerBasicDTO::new)
                .collect(Collectors.toCollection(ArrayList::new));

        return createMatchGroups(searchingPlayers, matchRequest) ;
    }

    private MatchResponse createMatchGroups(List<PlayerBasicDTO> players, MatchRequest matchRequest) {

        int groupSize = matchRequest.getGroupSize();
        List<List<PlayerBasicDTO>> matchGroups = new ArrayList<>();
        List<PlayerBasicDTO> currentGroup = new ArrayList<>();
        System.out.println(matchRequest);
        players.sort((p1,p2) -> Double.compare(
                calculateScoreForPlayer(p2, matchRequest),
                calculateScoreForPlayer(p1, matchRequest)
        ));

        if(!matchRequest.isMatchTypeFair()) {

            for (PlayerBasicDTO player : players) {

                currentGroup.add(player);
                if (currentGroup.size() == groupSize) {
                    matchGroups.add(new ArrayList<>(currentGroup));
                    currentGroup.clear();
                }
            }
            if (!currentGroup.isEmpty()) {

                matchGroups.add(new ArrayList<>(currentGroup));
                currentGroup.clear();
            }
            System.out.println(matchGroups);
        } else {

            int left = 0;
            int right = players.size() - 1;

            while (left <= right) {

                for(int i = 0; i < groupSize && left <= right; i++) {

                    if(i % 2 == 0) {
                        currentGroup.add(players.get(left++));
                    } else {
                        currentGroup.add(players.get(right--));
                    }
                }
                matchGroups.add(new ArrayList<>(currentGroup));
                currentGroup.clear();
            }
        }

        return new MatchResponse(matchGroups);
    }

    private double calculateScoreForPlayer(PlayerBasicDTO player, MatchRequest matchRequest) {

        double totalScore = 0.0;
        double totalWeight = 0.0;

        for(Map.Entry<String, Object> entry: matchRequest.getTargetAttributes().entrySet()) {

            String attributeKey = entry.getKey();
            Object targetValue = entry.getValue();
            Object playerValue = player.getAttributes().get(attributeKey);
            double weight = matchRequest.getAttributeWeights().get(attributeKey);

            double attributeScore = 0.0;

            if(playerValue instanceof Number && targetValue instanceof Number) {

                double playerVal = ((Number) playerValue).doubleValue();
                double targetVal = ((Number) targetValue).doubleValue();

                attributeScore = 1 - Math.abs(playerVal - targetVal)/Math.max(playerVal, targetVal);
            }
            if(playerValue != null && playerValue.equals(targetValue)) {

                attributeScore = 1.0;
            }

            totalScore += attributeScore * weight;
            totalWeight += weight;
        }

        return totalWeight > 0 ? totalScore / totalWeight : 0.0;
    }

    public MatchResponse getGroupsFromCustomIds(MatchRequest matchRequest) {

        return new MatchResponse();
    }
}
