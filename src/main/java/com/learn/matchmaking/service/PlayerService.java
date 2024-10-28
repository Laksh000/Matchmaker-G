package com.learn.matchmaking.service;

import com.learn.matchmaking.constant.PlayerConstants;
import com.learn.matchmaking.exception.PlayerNotFoundException;
import com.learn.matchmaking.model.Player;
import com.learn.matchmaking.repo.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepo;

    @Autowired
    public PlayerService(PlayerRepository playerRepo) {

        this.playerRepo = playerRepo;
    }

    public List<Player> getPlayers() {

        return playerRepo.findAll();
    }

    public Player getPlayer(String name) {

        return playerRepo.findByName(name);
    }

    public String registerPlayers(List<Player> players) {

        //error messages to be converted to constants
        try {
            playerRepo.saveAll(players);
            return PlayerConstants.SAVE_SUCCESS_MESSAGE;
        } catch (Exception e) {
            return PlayerConstants.SAVE_FAILURE_MESSAGE + e.getMessage();
        }

    }

    public String updatePlayers(List<Player> players) {

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
                                          if(updatedPlayer.getAttributes() != null)
                                              player.getAttributes().putAll(updatedPlayer.getAttributes());
                                      }

                                      return player;
                                  } catch (PlayerNotFoundException pe) {
                                      missingPlayers.add(pe.getMessage());
                                      return null;
                                  }
                              }
                        ).filter(player -> player!=null)
                        .toList();
        playerRepo.saveAll(updatedPlayers);

        if(missingPlayers.isEmpty()){
            return PlayerConstants.UPDATE_SUCCESS_MESSAGE;
        } else {
            return PlayerConstants.UPDATE_SUCCESS_MESSAGE + PlayerConstants.UPDATE_FAILURE_MESSAGE + String.join(", ", missingPlayers);
        }
    }

}
