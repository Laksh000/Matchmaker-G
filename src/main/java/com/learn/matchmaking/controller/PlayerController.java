package com.learn.matchmaking.controller;

import com.learn.matchmaking.model.PlayerBasicDTO;
import com.learn.matchmaking.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

        return playerService.getPlayers();
    }

    @GetMapping("{name}")
    public ResponseEntity<PlayerBasicDTO> getPlayer(@PathVariable String name) {

        return playerService.getPlayer(name);
    }

    @PostMapping("register")
    public ResponseEntity<String> addPlayers(@RequestBody List<PlayerBasicDTO> players) {

        return playerService.registerPlayers(players);
    }

    @PutMapping("update")
    public ResponseEntity<String> updatePlayers(@RequestBody List<PlayerBasicDTO> players) {

        return playerService.updatePlayers(players);
    }
}
