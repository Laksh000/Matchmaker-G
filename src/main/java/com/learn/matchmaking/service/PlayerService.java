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
import java.util.List;
import java.util.Objects;

@Service
public class PlayerService {

    private final PlayerRepository playerRepo;

    @Autowired
    public PlayerService(PlayerRepository playerRepo) {

        this.playerRepo = playerRepo;
    }

    public ResponseEntity<List<PlayerBasicDTO>> getPlayers() {

        List<PlayerBasicDTO> playersBasicDTO = playerRepo.findAll().stream()
                .map(PlayerBasicDTO::new)
                .toList();

        if(!playersBasicDTO.isEmpty()){

            return ResponseEntity.ok(playersBasicDTO);
        } else {

            return ResponseEntity.noContent().build();
        }
    }

    public ResponseEntity<PlayerBasicDTO> getPlayer(String name) {

        PlayerBasicDTO playerBasicDTO = new PlayerBasicDTO(playerRepo.findByName(name));

        if (playerBasicDTO.getId() == null) {

            return ResponseEntity.notFound().build();
        } else {

            return ResponseEntity.ok(playerBasicDTO);
        }
    }

    public ResponseEntity<String> registerPlayers(List<PlayerDTO> playersDTO) {

        try {
            List<Player> players = playersDTO.stream()
                            .map(Player::new)
                            .toList();
            playerRepo.saveAll(players);

            return new ResponseEntity<>(PlayerConstants.SAVE_SUCCESS_MESSAGE, HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(PlayerConstants.SAVE_FAILURE_MESSAGE + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<String> updatePlayers(List<PlayerDTO> players) {

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
                                          if(player.getAttributes() == null)
                                              player.setAttributes(updatedPlayer.getAttributes());
                                          if(updatedPlayer.getAttributes() != null)
                                              player.getAttributes().putAll(updatedPlayer.getAttributes());
                                          if(updatedPlayer.getId() != null)
                                              player.setIsSearchingForMatch(updatedPlayer.getIsSearchingForMatch());
                                      }

                                      return player;
                                  } catch (PlayerNotFoundException pe) {
                                      missingPlayers.add(pe.getMessage());
                                      return null;
                                  }
                              }
                        ).filter(Objects::nonNull)
                        .toList();
        playerRepo.saveAll(updatedPlayers);

        if(missingPlayers.isEmpty()){
            return new ResponseEntity<>(PlayerConstants.UPDATE_SUCCESS_MESSAGE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(PlayerConstants.UPDATE_SUCCESS_MESSAGE + PlayerConstants.UPDATE_FAILURE_MESSAGE
                    + String.join(", ", missingPlayers), HttpStatus.BAD_REQUEST);
        }
    }

}
