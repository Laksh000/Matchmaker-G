package com.learn.matchmaking.controller;

import com.learn.matchmaking.constant.PlayerConstants;
import com.learn.matchmaking.dto.PlayerBasicDTO;
import com.learn.matchmaking.dto.PlayerDTO;
import com.learn.matchmaking.exception.PlayerNotFoundException;
import com.learn.matchmaking.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("players")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {

        this.playerService = playerService;
    }

    @GetMapping("all")
    public ResponseEntity<List<PlayerBasicDTO>> getPlayers() {

        if(!playerService.getPlayers().isEmpty()) {

            return ResponseEntity.ok(playerService.getPlayers());
        } else {

            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("{name}")
    public ResponseEntity<PlayerBasicDTO> getPlayer(@PathVariable String name) {

        try {

            return ResponseEntity.ok(playerService.getPlayer(name));
        } catch (PlayerNotFoundException e) {

            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("register")
    public ResponseEntity<String> addPlayers(@RequestBody List<PlayerDTO> players) {

        String message = playerService.registerPlayers(players);

        if(message.equals(PlayerConstants.SAVE_SUCCESS_MESSAGE)) {

            return ResponseEntity.ok(message);
        } else {

            return ResponseEntity.badRequest().body(message);
        }
    }

    @PutMapping("update")
    public ResponseEntity<String> updatePlayers(@RequestBody List<PlayerDTO> players) {

        String message = playerService.updatePlayers(players);

        if(message.equals(PlayerConstants.UPDATE_FAILURE_MESSAGE)) {

            return ResponseEntity.ok(message);
        } else {

            return ResponseEntity.badRequest().body(message);
        }
    }
}
