package com.learn.matchmaking.service;

import com.learn.matchmaking.constant.MatchConstants;
import com.learn.matchmaking.dto.MatchRequest;
import com.learn.matchmaking.dto.MatchResponse;
import com.learn.matchmaking.dto.PlayerBasicDTO;
import com.learn.matchmaking.exception.PlayerNotFoundException;
import com.learn.matchmaking.model.Player;
import com.learn.matchmaking.repo.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        int activePlayersCount = searchingPlayers.size();

        if(activePlayersCount <= 1 && matchRequest.getGroupSize() >= activePlayersCount) {

            return new MatchResponse(new ArrayList<>(), MatchConstants.MATCH_MAKING_CRITERIA_MESSAGE);
        }

        return createMatchGroups(searchingPlayers, matchRequest);
    }

    public MatchResponse getGroupsFromCustomIds(MatchRequest matchRequest) {

        List<String> customPlayerIds = matchRequest.getPlayerIds();
        if(customPlayerIds == null) {

            return new MatchResponse(new ArrayList<>(), MatchConstants.MATCH_PLAYER_IDS_MANDATORY_MESSAGE);
        }
        List<PlayerBasicDTO> customPlayersDetails = new ArrayList<>();

        for(String customPlayerId : customPlayerIds) {

            try {

                Optional<Player> player = playerRepo.findById(customPlayerId);
                if (player.isEmpty()) {
                    throw new PlayerNotFoundException("Player with ID " + customPlayerId + " not found");
                }
                customPlayersDetails.add(new PlayerBasicDTO(player.get()));

            }  catch (Exception e) {

                return new MatchResponse(new ArrayList<>(), e.getMessage());

            }
        }

        return createMatchGroups(customPlayersDetails, matchRequest);
    }

    private MatchResponse createMatchGroups(List<PlayerBasicDTO> players, MatchRequest matchRequest) {

        int groupSize = matchRequest.getGroupSize();
        List<List<PlayerBasicDTO>> matchGroups = new ArrayList<>();
        List<PlayerBasicDTO> currentGroup = new ArrayList<>();

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

        return new MatchResponse(matchGroups, MatchConstants.MATCH_SUCCESSFUL_MESSAGE);
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
}
