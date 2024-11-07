package com.learn.matchmaking.service;

import com.learn.matchmaking.constant.PlayerConstants;
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

    public ResponseEntity<MatchResponse> getGroupsFromPool(MatchRequest matchRequest) {

        List<PlayerBasicDTO> searchingPlayers = playerRepo.findByIsSearchingForMatch(true)
                .stream().map(PlayerBasicDTO::new)
                .collect(Collectors.toCollection(ArrayList::new));

        return new ResponseEntity<>(createMatchGroups(searchingPlayers, matchRequest), HttpStatus.OK) ;
    }

    public ResponseEntity<MatchResponse> getGroupsFromCustomIds(MatchRequest matchRequest) {

        List<String> customPlayerIds = matchRequest.getPlayerIds();
        if(customPlayerIds.isEmpty()) {

            return new ResponseEntity<>(new MatchResponse(new ArrayList<>(), "Player Id's are mandatory"),HttpStatus.NO_CONTENT);
        }
        List<PlayerBasicDTO> customPlayersDetails = new ArrayList<>();

        for(String customPlayerId : customPlayerIds) {

            try {

                Player player = playerRepo.findById(customPlayerId).orElse(null);
                if (player == null) {
                    throw new PlayerNotFoundException("Player with ID " + customPlayerId + " not found");
                }
                customPlayersDetails.add(new PlayerBasicDTO(player));

            }  catch (Exception e) {

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            }
        }

        return new ResponseEntity<>(createMatchGroups(customPlayersDetails, matchRequest), HttpStatus.OK);
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

        return new MatchResponse(matchGroups,"Matchmaking was Successful");
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
