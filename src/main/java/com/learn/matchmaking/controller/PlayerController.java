package com.learn.matchmaking.controller;

import com.learn.matchmaking.constant.PlayerConstants;
import com.learn.matchmaking.dto.PlayerBasicDTO;
import com.learn.matchmaking.dto.PlayerDTO;
import com.learn.matchmaking.exception.PlayerNotFoundException;
import com.learn.matchmaking.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Player Management")
@RestController
@RequestMapping("players")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {

        this.playerService = playerService;
    }

    @Operation(summary = "Get all players")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "List of all players present",
                    content = {@Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PlayerBasicDTO.class)
                    ))}
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "There are no players present",
                    content = {@Content}
            ),
            @ApiResponse(
                    responseCode = "403", description = "No Authorization",
                    content = {@Content}
            )
    })
    @GetMapping("all")
    public ResponseEntity<List<PlayerBasicDTO>> getPlayers() {

        if(!playerService.getPlayers().isEmpty()) {

            return ResponseEntity.ok(playerService.getPlayers());
        } else {

            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "Get player by name (player name is unique)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Player with name found",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PlayerBasicDTO.class)
                            )}
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Player with name not found",
                    content = {@Content}
            ),
            @ApiResponse(
                    responseCode = "403", description = "No Authorization",
                    content = {@Content}
            )
    })
    @GetMapping("{name}")
    public ResponseEntity<PlayerBasicDTO> getPlayer(@PathVariable String name) {

        try {

            return ResponseEntity.ok(playerService.getPlayer(name));
        } catch (PlayerNotFoundException e) {

            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "Register players")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Registration of players is successful",
                    content = {@Content}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Registration failed: with the player names whose registration failed",
                    content = {@Content}
            ),
            @ApiResponse(
                    responseCode = "403", description = "No Authorization",
                    content = {@Content}
            )
    })
    @PostMapping("register")
    public ResponseEntity<String> addPlayers(@RequestBody List<PlayerDTO> players) {

        String message = playerService.registerPlayers(players);

        if(message.equals(PlayerConstants.SAVE_SUCCESS_MESSAGE)) {

            return ResponseEntity.ok(message);
        } else {

            return ResponseEntity.badRequest().body(message);
        }
    }

    @Operation(summary = "Update players")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Update of players is successful",
                    content = {@Content}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Update failed: player not found",
                    content = {@Content}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Update failed: Player id is null",
                    content = {@Content}
            ),
            @ApiResponse(
                    responseCode = "403", description = "No Authorization",
                    content = {@Content}
            )
    })
    @PutMapping("update")
    public ResponseEntity<String> updatePlayers(@RequestBody List<PlayerDTO> players) {

        String message = playerService.updatePlayers(players);

        if(message.equals(PlayerConstants.UPDATE_SUCCESS_MESSAGE)) {

            return ResponseEntity.ok(message);
        } else {

            return ResponseEntity.badRequest().body(message);
        }
    }

    @Operation(summary = "Delete players")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Deletion of players is successful",
                    content = {@Content}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Deletion failed: player not found",
                    content = {@Content}
            ),
            @ApiResponse(
                    responseCode = "403", description = "No Authorization",
                    content = {@Content}
            )
    })
    @DeleteMapping("delete")
    public ResponseEntity<String> deletePlayers(@RequestBody List<String> playerIds) {

        String message = playerService.deletePlayers(playerIds);

        if(message.equals(PlayerConstants.DELETE_SUCCESSFUL_MESSAGE)) {

            return ResponseEntity.ok(message);
        } else {

            return ResponseEntity.badRequest().body(message);
        }
    }
}
