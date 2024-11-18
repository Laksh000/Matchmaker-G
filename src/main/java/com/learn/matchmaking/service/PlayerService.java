package com.learn.matchmaking.service;

import com.learn.matchmaking.constant.PlayerConstants;
import com.learn.matchmaking.dto.PlayerDTO;
import com.learn.matchmaking.exception.PlayerNotFoundException;
import com.learn.matchmaking.model.Player;
import com.learn.matchmaking.dto.PlayerBasicDTO;
import com.learn.matchmaking.repo.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

        return playerRepo.findByName(name)
                .map(PlayerBasicDTO::new)
                .orElseThrow(() -> new PlayerNotFoundException("Player with name " + name + " not found"));
    }

    public String registerPlayers(List<PlayerDTO> playersDTO) {

            List<String> duplicatePlayers = new ArrayList<>();
            List<Player> players = playersDTO.stream()
                            .map(
                                    playerDTO -> {
                                            Optional<Player> player = playerRepo.findByName(playerDTO.getName());
                                            if (player.isPresent()) {

                                                duplicatePlayers.add(playerDTO.getName());
                                                return null;
                                            } else {

                                                return new Player(playerDTO);
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

        AtomicInteger playerObjectWithIdNull = new AtomicInteger();
        List<String> missingPlayers = new ArrayList<>();
        List<Player> updatedPlayers = players.stream()
                        .map(
                              updatedPlayer -> {
                                  try {
                                      Player player = null;
                                      String playerId = Optional.ofNullable(updatedPlayer.getId())
                                              .orElseThrow(() -> new NullPointerException("Player id is null"));

                                      if(!playerId.isEmpty()) {
                                          player = playerRepo.findById(playerId)
                                                  .orElseThrow(() -> new PlayerNotFoundException(updatedPlayer.getId()));
                                          if (player != null) {

                                              if (updatedPlayer.getName() != null)
                                                  player.setName(updatedPlayer.getName());
                                              if (updatedPlayer.getIsSearchingForMatch() != null)
                                                  player.setIsSearchingForMatch(updatedPlayer.getIsSearchingForMatch());
                                              if (updatedPlayer.getAttributes() != null) {
                                                  if (player.getAttributes() == null) {
                                                      player.setAttributes(updatedPlayer.getAttributes());
                                                  } else {
                                                      player.getAttributes().putAll(updatedPlayer.getAttributes());
                                                  }
                                              }
                                          }
                                      }

                                      return player;
                                  } catch (PlayerNotFoundException pe) {
                                      missingPlayers.add(pe.getMessage());
                                      return null;
                                  } catch (NullPointerException npe) {
                                      playerObjectWithIdNull.getAndIncrement();
                                      return null;
                                  }
                              }
                        ).filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
        playerRepo.saveAll(updatedPlayers);

        if(missingPlayers.isEmpty() && playerObjectWithIdNull.get() == 0){

            return PlayerConstants.UPDATE_SUCCESS_MESSAGE;
        } else if (playerObjectWithIdNull.get() > 0) {

            return String.format(PlayerConstants.UPDATE_FAILURE_MESSAGE2, playerObjectWithIdNull.get());
        } else {

            return PlayerConstants.UPDATE_FAILURE_MESSAGE
                    + String.join(", ", missingPlayers);
        }
    }

}
