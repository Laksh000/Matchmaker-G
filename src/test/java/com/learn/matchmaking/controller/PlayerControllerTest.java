package com.learn.matchmaking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.matchmaking.constant.PlayerConstants;
import com.learn.matchmaking.dto.PlayerBasicDTO;
import com.learn.matchmaking.dto.PlayerDTO;
import com.learn.matchmaking.exception.PlayerNotFoundException;
import com.learn.matchmaking.model.Player;
import com.learn.matchmaking.service.PlayerService;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PlayerController.class)
class PlayerControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private PlayerService playerService;

    @Autowired
    public PlayerControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {

        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    void canGetPlayers() throws Exception {

        //given
        PlayerBasicDTO player1DTO = new PlayerBasicDTO();
        player1DTO.setId("kjdshfGIkhvfytvf");
        player1DTO.setName("Player1");
        player1DTO.setAttributes(
                new HashMap<>(Map.of(
                        "strength", 85,
                        "speed", 92
                ))
        );
        PlayerBasicDTO player2DTO = new PlayerBasicDTO();
        player2DTO.setId("auebvgavbiu");
        player2DTO.setName("Player2");
        player2DTO.setAttributes(
                new HashMap<>(Map.of(
                        "strength",75 ,
                        "speed", 69
                ))
        );
        List<PlayerBasicDTO> players = List.of(player1DTO, player2DTO);

        //when
        when(playerService.getPlayers()).thenReturn(players);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/players/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(player1DTO.getName()))
                .andExpect(jsonPath("$[1].name").value(player2DTO.getName()));
    }

    @Test
    void canNotGetPlayers() throws Exception {

        //given
        List<PlayerBasicDTO> players = Collections.emptyList();

        //when
        when(playerService.getPlayers()).thenReturn(players);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/players/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void canGetPlayer() throws Exception {

        //given
        String name = "Player1";
        PlayerBasicDTO playerBasicDTO = new PlayerBasicDTO();
        playerBasicDTO.setId("kjdshfGI");
        playerBasicDTO.setName(name);
        playerBasicDTO.setAttributes(
                Map.of(
                        "strength", 85,
                        "speed", 92
                )
        );

        //when
        when(playerService.getPlayer(playerBasicDTO.getName())).thenReturn(playerBasicDTO);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/players/{name}", playerBasicDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(playerBasicDTO.getName()));
    }

    @Test
    void canNotGetPlayer() throws Exception {

        //given
        String name = "Player1";

        //when
        when(playerService.getPlayer(name)).thenThrow(new PlayerNotFoundException("Player with name " + name + " not found"));

        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/players/{name}", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void canAddPlayers() throws Exception {

        //given
        PlayerDTO player1DTO = new PlayerDTO();
        player1DTO.setId("kjdshfGIkhvfytvf");
        player1DTO.setName("Player1");
        player1DTO.setAttributes(
                new HashMap<>(Map.of(
                        "strength", 85,
                        "speed", 92
                ))
        );
        PlayerDTO player2DTO = new PlayerDTO();
        player2DTO.setId("auebvgavbiu");
        player2DTO.setName("Player2");
        player2DTO.setAttributes(
                new HashMap<>(Map.of(
                        "strength",75 ,
                        "speed", 69
                ))
        );
        List<PlayerDTO> players = List.of(player1DTO, player2DTO);
        String playersJSON = objectMapper.writeValueAsString(players);

        //when
        when(playerService.registerPlayers(players)).thenReturn(PlayerConstants.SAVE_SUCCESS_MESSAGE);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/players/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(playersJSON))
                .andExpect(status().isOk());
    }

    @Test
    void canNotAddPlayers() throws Exception {

        //given
        PlayerDTO player1DTO = new PlayerDTO();
        player1DTO.setId("kjdshfGIkhvfytvf");
        player1DTO.setName("Player1");
        player1DTO.setAttributes(
                new HashMap<>(Map.of(
                        "strength", 85,
                        "speed", 92
                ))
        );
        PlayerDTO player2DTO = new PlayerDTO();
        player2DTO.setId("auebvgavbiu");
        player2DTO.setName("Player2");
        player2DTO.setAttributes(
                new HashMap<>(Map.of(
                        "strength",75 ,
                        "speed", 69
                ))
        );
        List<PlayerDTO> players = List.of(player1DTO, player2DTO);
        String failureMessage = PlayerConstants.SAVE_FAILURE_MESSAGE + "Player1 as players with the same name already exists";
        String playersJSON = objectMapper.writeValueAsString(players);

        //when
        when(playerService.registerPlayers(players)).thenReturn(failureMessage);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/players/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playersJSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void canUpdatePlayers() throws Exception {

        //given
        PlayerDTO player1DTO = new PlayerDTO();
        player1DTO.setId("kjdshfGIkhvfytvf");
        player1DTO.setAttributes(
                new HashMap<>(Map.of(
                        "strength", 85,
                        "speed", 92
                ))
        );
        PlayerDTO player2DTO = new PlayerDTO();
        player2DTO.setId("auebvgavbiu");
        player2DTO.setName("Player2updated");
        List<PlayerDTO> players = List.of(player1DTO, player2DTO);
        String playersJSON = objectMapper.writeValueAsString(players);

        //when
        when(playerService.updatePlayers(players)).thenReturn(PlayerConstants.UPDATE_SUCCESS_MESSAGE);

        //then
        mockMvc.perform(MockMvcRequestBuilders.put("/players/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(playersJSON))
                .andExpect(status().isOk());
    }

    @Test
    void canNotUpdatePlayers() throws Exception {

        //given
        PlayerDTO player1DTO = new PlayerDTO();
        player1DTO.setId("kjdshfGIkhvfytvf");
        player1DTO.setAttributes(
                new HashMap<>(Map.of(
                        "strength", 85,
                        "speed", 92
                ))
        );
        PlayerDTO player2DTO = new PlayerDTO();
        player2DTO.setId("auebvgavbiu");
        player2DTO.setName("Player2updated");
        List<PlayerDTO> players = List.of(player1DTO, player2DTO);
        String failureMessage = PlayerConstants.UPDATE_FAILURE_MESSAGE + "Player2";
        String playersJSON = objectMapper.writeValueAsString(players);

        //when
        when(playerService.updatePlayers(players)).thenReturn(failureMessage);

        //then
        mockMvc.perform(MockMvcRequestBuilders.put("/players/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(playersJSON))
                .andExpect(status().isBadRequest());
    }
}