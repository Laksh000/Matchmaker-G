package com.learn.matchmaking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.matchmaking.constant.MatchConstants;
import com.learn.matchmaking.dto.MatchRequest;
import com.learn.matchmaking.dto.MatchResponse;
import com.learn.matchmaking.dto.PlayerBasicDTO;
import com.learn.matchmaking.service.MatchService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MatchController.class)
class MatchControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private MatchService matchService;

    @Autowired
    public MatchControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {

        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    void canMatchGroupFromPool() throws Exception {

        //given
        MatchRequest request = getRequestWithOutIds();
        PlayerBasicDTO playerBasicDTO1 = getBasicDTO1();
        PlayerBasicDTO playerBasicDTO2 = getBasicDTO2();
        PlayerBasicDTO playerBasicDTO3 = getBasicDTO3();
        PlayerBasicDTO playerBasicDTO4 = getBasicDTO4();
        List<List<PlayerBasicDTO>> playersDTO = List.of(
                List.of(playerBasicDTO1, playerBasicDTO4),
                List.of(playerBasicDTO3, playerBasicDTO2)
        );
        MatchResponse response = new MatchResponse(playersDTO, MatchConstants.MATCH_SUCCESSFUL_MESSAGE);
        String matchJson = objectMapper.writeValueAsString(request);

        //when
        when(matchService.getGroupsFromPool(request)).thenReturn(response);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/match/pool")
                .contentType(MediaType.APPLICATION_JSON)
                .content(matchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MatchConstants.MATCH_SUCCESSFUL_MESSAGE))
                .andExpect(jsonPath("$.groups[0][0].name").value("Player1"))
                .andExpect(jsonPath("$.groups[0][1].name").value("Player4"))
                .andExpect(jsonPath("$.groups[1][0].name").value("Player3"))
                .andExpect(jsonPath("$.groups[1][1].name").value("Player2"));
    }

    @Test
    void canNotMatchGroupFromPool() throws Exception {

        //given
        MatchRequest request = getRequestWithOutIds();
        MatchResponse response = new MatchResponse(new ArrayList<>(), MatchConstants.MATCH_MAKING_CRITERIA_MESSAGE);
        String matchJSON = objectMapper.writeValueAsString(request);

        //when
        when(matchService.getGroupsFromPool(request)).thenReturn(response);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/match/pool")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(matchJSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MatchConstants.MATCH_MAKING_CRITERIA_MESSAGE));
    }

    @Test
    void canMatchGroupFromGivenIds() throws Exception {

        MatchRequest request = getRequestWithIds();
        PlayerBasicDTO playerBasicDTO1 = getBasicDTO1();
        PlayerBasicDTO playerBasicDTO2 = getBasicDTO2();
        PlayerBasicDTO playerBasicDTO3 = getBasicDTO3();
        PlayerBasicDTO playerBasicDTO4 = getBasicDTO4();
        List<List<PlayerBasicDTO>> playersDTO = List.of(
                List.of(playerBasicDTO1, playerBasicDTO4),
                List.of(playerBasicDTO3, playerBasicDTO2)
        );
        String matchJson = objectMapper.writeValueAsString(request);
        MatchResponse response = new MatchResponse(playersDTO, MatchConstants.MATCH_SUCCESSFUL_MESSAGE);

        //when
        when(matchService.getGroupsFromCustomIds(request)).thenReturn(response);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/match/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(matchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MatchConstants.MATCH_SUCCESSFUL_MESSAGE))
                .andExpect(jsonPath("$.groups[0][0].name").value("Player1"))
                .andExpect(jsonPath("$.groups[0][1].name").value("Player4"))
                .andExpect(jsonPath("$.groups[1][0].name").value("Player3"))
                .andExpect(jsonPath("$.groups[1][1].name").value("Player2"));
    }

    @Test
    void canNotMatchGroupFromGivenIds() throws Exception {

        //given
        MatchRequest request = getRequestWithOutIds();
        MatchResponse response = new MatchResponse(new ArrayList<>(), MatchConstants.MATCH_PLAYER_IDS_MANDATORY_MESSAGE);
        String matchJSON = objectMapper.writeValueAsString(request);

        //when
        when(matchService.getGroupsFromCustomIds(request)).thenReturn(response);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/match/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(matchJSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MatchConstants.MATCH_PLAYER_IDS_MANDATORY_MESSAGE));
    }

    private static  PlayerBasicDTO getBasicDTO4() {
        PlayerBasicDTO playerBasicDTO4 = new PlayerBasicDTO();
        playerBasicDTO4.setId("672a1754b2eeb2739fa1bb07");
        playerBasicDTO4.setName("Player4");
        playerBasicDTO4.setAttributes(
                new HashMap<>(
                        Map.of(
                                "strength", 40,
                                "speed", 60,
                                "isVIP", false,
                                "experiencePoints", 1200,
                                "specialAbility", "Healing"
                        )
                )
        );
        return playerBasicDTO4;
    }

    private static  PlayerBasicDTO getBasicDTO3() {
        PlayerBasicDTO playerBasicDTO3 = new PlayerBasicDTO();
        playerBasicDTO3.setId("672a1754b2eeb2739fa1bb06");
        playerBasicDTO3.setName("Player3");
        playerBasicDTO3.setAttributes(
                new HashMap<>(
                        Map.of(
                                "strength", 95,
                                "speed", 80,
                                "isVIP", true,
                                "experiencePoints", 3000,
                                "specialAbility", "Shield"
                        )
                )
        );
        return playerBasicDTO3;
    }

    private static  PlayerBasicDTO getBasicDTO2() {
        PlayerBasicDTO playerBasicDTO2 = new PlayerBasicDTO();
        playerBasicDTO2.setId("672a1754b2eeb2739fa1bb05");
        playerBasicDTO2.setName("Player2");
        playerBasicDTO2.setAttributes(
                new HashMap<>(
                        Map.of(
                                "strength", 60,
                                "speed", 75,
                                "isVIP", false,
                                "experiencePoints", 1500,
                                "specialAbility", "Fire"
                        )
                )
        );
        return playerBasicDTO2;
    }

    private static  PlayerBasicDTO getBasicDTO1() {
        PlayerBasicDTO playerBasicDTO1 = new PlayerBasicDTO();
        playerBasicDTO1.setId("672a1754b2eeb2739fa1bb04");
        playerBasicDTO1.setName("Player1");
        playerBasicDTO1.setAttributes(
                new HashMap<>(
                        Map.of(
                                "strength", 85,
                                "speed", 92,
                                "isVIP", true,
                                "experiencePoints", 2500,
                                "specialAbility", "Invisibility"
                        )
                )
        );
        return playerBasicDTO1;
    }

    private static  MatchRequest getRequestWithOutIds() {
        MatchRequest request = new MatchRequest();
        request.setMatchTypeFair(true);
        request.setGroupSize(2);
        request.setTargetAttributes(
                new HashMap<>(
                        Map.of(
                                "strength", 80,
                                "speed", 85,
                                "isVIP", true,
                                "experiencePoints", 2000
                        )
                )
        );
        request.setAttributeWeights(
                new HashMap<>(
                        Map.of(
                                "strength", 0.4,
                                "speed", 0.3,
                                "isVIP", 0.2,
                                "experiencePoints", 0.1
                        )
                )
        );
        return request;
    }

    private static  MatchRequest getRequestWithIds() {
        MatchRequest request = new MatchRequest();
        request.setMatchTypeFair(true);
        request.setPlayerIds(new ArrayList<>(
                List.of("672a1754b2eeb2739fa1bb04", "672a1754b2eeb2739fa1bb05", "672a1754b2eeb2739fa1bb06", "672a1754b2eeb2739fa1bb07")
        ));
        request.setGroupSize(2);
        request.setTargetAttributes(
                new HashMap<>(
                        Map.of(
                                "strength", 80,
                                "speed", 85,
                                "isVIP", true,
                                "experiencePoints", 2000
                        )
                )
        );
        request.setAttributeWeights(
                new HashMap<>(
                        Map.of(
                                "strength", 0.4,
                                "speed", 0.3,
                                "isVIP", 0.2,
                                "experiencePoints", 0.1
                        )
                )
        );
        return request;
    }
}