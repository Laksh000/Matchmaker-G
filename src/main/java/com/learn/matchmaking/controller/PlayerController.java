package com.learn.matchmaking.controller;

import com.learn.matchmaking.model.Player;
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
    public ResponseEntity<List<Player>> getPlayers() {

        return new ResponseEntity<>(playerService.getPlayers(), HttpStatus.OK);
    }

    @GetMapping("{name}")
    public ResponseEntity<Player> getPlayer(@PathVariable String name) {

        return new ResponseEntity<>(playerService.getPlayer(name), HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<String> addPlayers(@RequestBody List<Player> players) {

        return new ResponseEntity<>(playerService.registerPlayers(players), HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<String> updatePlayers(@RequestBody List<Player> players) {

        return new ResponseEntity<>(playerService.updatePlayers(players), HttpStatus.OK);
    }
}
