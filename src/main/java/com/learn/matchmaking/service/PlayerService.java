package com.learn.matchmaking.service;

import com.learn.matchmaking.constant.PlayerConstants;
import com.learn.matchmaking.dto.PlayerDTO;
import com.learn.matchmaking.exception.PlayerNotFoundException;
import com.learn.matchmaking.model.Player;
import com.learn.matchmaking.dto.PlayerBasicDTO;
import com.learn.matchmaking.repo.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private final PlayerRepository playerRepo;

    @Autowired
    public PlayerService(PlayerRepository playerRepo) {

        this.playerRepo = playerRepo;
    }

    public List<PlayerBasicDTO> getPlayers() {

        return playerRepo.findAll().stream()
                .map(PlayerBasicDTO::new)
                .toList();
    }

    public PlayerBasicDTO getPlayer(String name) {

        PlayerBasicDTO playerBasicDTO = new PlayerBasicDTO(playerRepo.findByName(name));

        if (playerBasicDTO.getId() == null) {

            throw new PlayerNotFoundException("Player with name " + name + " not found");
        } else {

            return playerBasicDTO;
        }
    }

    public String registerPlayers(List<PlayerDTO> playersDTO) {

            List<String> duplicatePlayers = new ArrayList<>();
            List<Player> players = playersDTO.stream()
                            .map(
                                    playerDTO -> {
                                            Player player = playerRepo.findByName(playerDTO.getName());

                                            if (player != null) {

                                                duplicatePlayers.add(playerDTO.getName());
                                                return null;
                                            } else {

                                                return player;
                                            }
                                    }
                            ).filter(Objects::nonNull)
                            .toList();
            playerRepo.saveAll(players);

        if(duplicatePlayers.isEmpty()) {

            return PlayerConstants.SAVE_SUCCESS_MESSAGE;
        } else {

            return PlayerConstants.SAVE_FAILURE_MESSAGE + String.join(", ", duplicatePlayers
                    + " as players with the same name already exists" );
        }

    }

    public String updatePlayers(List<PlayerDTO> players) {

        List<String> missingPlayers = new ArrayList<>();
        List<Player> updatedPlayers = players.stream()
                        .map(
                              updatedPlayer -> {
                                  try {
                                      Player player = playerRepo.findById(updatedPlayer.getId())
                                              .orElseThrow(() -> new PlayerNotFoundException(updatedPlayer.getId()));
                                      if (player != null) {

                                          if (updatedPlayer.getName() != null)
                                              player.setName(updatedPlayer.getName());
                                          if(updatedPlayer.getIsSearchingForMatch() != null)
                                              player.setIsSearchingForMatch(updatedPlayer.getIsSearchingForMatch());
                                          if (updatedPlayer.getAttributes() != null) {
                                              if (player.getAttributes() == null) {
                                                  player.setAttributes(updatedPlayer.getAttributes());
                                              } else {
                                                  player.getAttributes().putAll(updatedPlayer.getAttributes());
                                              }
                                          }
                                      }

                                      return player;
                                  } catch (PlayerNotFoundException pe) {
                                      missingPlayers.add(pe.getMessage());
                                      return null;
                                  }
                              }
                        ).filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
        playerRepo.saveAll(updatedPlayers);

        if(missingPlayers.isEmpty()){

            return PlayerConstants.UPDATE_SUCCESS_MESSAGE;
        } else {

            return PlayerConstants.UPDATE_FAILURE_MESSAGE
                    + String.join(", ", missingPlayers);
        }
    }

}
