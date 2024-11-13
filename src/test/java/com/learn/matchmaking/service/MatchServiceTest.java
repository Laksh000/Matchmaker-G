package com.learn.matchmaking.service;

import com.learn.matchmaking.constant.MatchConstants;
import com.learn.matchmaking.dto.MatchRequest;
import com.learn.matchmaking.dto.MatchResponse;
import com.learn.matchmaking.dto.PlayerBasicDTO;
import com.learn.matchmaking.model.Player;
import com.learn.matchmaking.repo.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private PlayerRepository playerRepo;
    private MatchService matchService;

    @BeforeEach
    void setUp() {

        matchService = new MatchService(playerRepo);
    }

    @Test
    void canGetGroupsFromPoolWithMatchTypeFairTrue() {

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
        List<Player> players = List.of(playerBasicDTO1, playerBasicDTO2, playerBasicDTO3, playerBasicDTO4)
                .stream()
                .map(Player::new).toList();
        List<List<PlayerBasicDTO>> playersDTO = List.of(
                List.of(playerBasicDTO1, playerBasicDTO4),
                List.of(playerBasicDTO3, playerBasicDTO2)
        );
        //when
        when(playerRepo.findByIsSearchingForMatch(true)).thenReturn(players);
        MatchResponse response = matchService.getGroupsFromPool(request);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getGroups().isEmpty()).isFalse();
        assertThat(response.getMessage().isEmpty()).isFalse();
        assertThat(response.getGroups().size()).isEqualTo(2);
        assertThat(response.getGroups()).isEqualTo(playersDTO);
        assertThat(response.getMessage()).isEqualTo(MatchConstants.MATCH_SUCCESSFUL_MESSAGE);
    }

    @Test
    void canGetGroupsFromPoolWithMatchTypeFairFalse() {

        MatchRequest request = new MatchRequest();
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
        List<Player> players = List.of(playerBasicDTO1, playerBasicDTO2, playerBasicDTO3, playerBasicDTO4)
                .stream()
                .map(Player::new).toList();
        List<List<PlayerBasicDTO>> playersDTO = List.of(
                List.of(playerBasicDTO1, playerBasicDTO3),
                List.of(playerBasicDTO2, playerBasicDTO4)
        );
        //when
        when(playerRepo.findByIsSearchingForMatch(true)).thenReturn(players);
        MatchResponse response = matchService.getGroupsFromPool(request);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getGroups().isEmpty()).isFalse();
        assertThat(response.getMessage().isEmpty()).isFalse();
        assertThat(response.getGroups().size()).isEqualTo(2);
        assertThat(response.getGroups()).isEqualTo(playersDTO);
        assertThat(response.getMessage()).isEqualTo(MatchConstants.MATCH_SUCCESSFUL_MESSAGE);
    }

    @Test
    void canNotGetGroupsFromPoolNotMatchingOfMatchMakingCriteria() {

        MatchRequest request = new MatchRequest();
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
        List<Player> players = new ArrayList<>();

        //when
        when(playerRepo.findByIsSearchingForMatch(true)).thenReturn(players);
        MatchResponse response = matchService.getGroupsFromPool(request);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getGroups().isEmpty()).isTrue();
        assertThat(response.getMessage().isEmpty()).isFalse();
        assertThat(response.getMessage()).isEqualTo(MatchConstants.MATCH_MAKING_CRITERIA_MESSAGE);
    }

    @Test
    void canGetGroupsFromCustomIdsWithMatchTypeFairTrue() {

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
        List<Player> players = List.of(playerBasicDTO1, playerBasicDTO2, playerBasicDTO3, playerBasicDTO4)
                .stream()
                .map(Player::new).toList();
        List<List<PlayerBasicDTO>> playersDTO = List.of(
                List.of(playerBasicDTO1, playerBasicDTO4),
                List.of(playerBasicDTO3, playerBasicDTO2)
        );

        //when
        for (Player player : players) {

            when(playerRepo.findById(player.getId())).thenReturn(Optional.of(player));
        }
        MatchResponse response = matchService.getGroupsFromCustomIds(request);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getGroups().isEmpty()).isFalse();
        assertThat(response.getMessage().isEmpty()).isFalse();
        assertThat(response.getGroups().size()).isEqualTo(2);
        assertThat(response.getGroups()).isEqualTo(playersDTO);
        assertThat(response.getMessage()).isEqualTo(MatchConstants.MATCH_SUCCESSFUL_MESSAGE);
    }

    @Test
    void canGetGroupsFromCustomIdsWithMatchTypeFairFalse() {

        MatchRequest request = new MatchRequest();
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
        List<Player> players = List.of(playerBasicDTO1, playerBasicDTO2, playerBasicDTO3, playerBasicDTO4)
                .stream()
                .map(Player::new).toList();
        List<List<PlayerBasicDTO>> playersDTO = List.of(
                List.of(playerBasicDTO1, playerBasicDTO3),
                List.of(playerBasicDTO2, playerBasicDTO4)
        );

        //when
        for (Player player : players) {

            when(playerRepo.findById(player.getId())).thenReturn(Optional.of(player));
        }
        MatchResponse response = matchService.getGroupsFromCustomIds(request);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getGroups().isEmpty()).isFalse();
        assertThat(response.getMessage().isEmpty()).isFalse();
        assertThat(response.getGroups().size()).isEqualTo(2);
        assertThat(response.getGroups()).isEqualTo(playersDTO);
        assertThat(response.getMessage()).isEqualTo(MatchConstants.MATCH_SUCCESSFUL_MESSAGE);
    }

    @Test
    void canGetGroupsFromCustomIdsWhenCustomIdsEmpty() {

        MatchRequest request = new MatchRequest();
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
        MatchResponse response = matchService.getGroupsFromCustomIds(request);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getGroups().isEmpty()).isTrue();
        assertThat(response.getMessage().isEmpty()).isFalse();
        assertThat(response.getMessage()).isEqualTo(MatchConstants.MATCH_PLAYER_IDS_MANDATORY_MESSAGE);
    }

    @Test
    void canGetGroupsFromCustomIdsPlayerNotFound() {

        MatchRequest request = new MatchRequest();
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

        //when
        when(playerRepo.findById(playerBasicDTO1.getId())).thenReturn(Optional.empty());
        MatchResponse response = matchService.getGroupsFromCustomIds(request);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getGroups().isEmpty()).isTrue();
        assertThat(response.getMessage().isEmpty()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Player with ID " + playerBasicDTO1.getId() + " not found");
    }
}