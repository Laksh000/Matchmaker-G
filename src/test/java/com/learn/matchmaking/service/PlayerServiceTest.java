package com.learn.matchmaking.service;

import com.learn.matchmaking.constant.PlayerConstants;
import com.learn.matchmaking.dto.PlayerBasicDTO;
import com.learn.matchmaking.dto.PlayerDTO;
import com.learn.matchmaking.exception.PlayerNotFoundException;
import com.learn.matchmaking.model.Player;
import com.learn.matchmaking.repo.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepo;
    private PlayerService playerService;

    @BeforeEach
    void setUp() {

        playerService = new PlayerService(playerRepo);
    }

    @Test
    void canGetPlayers() {

        //when
        playerService.getPlayers();
        //then
        verify(playerRepo).findAll();
    }

    @Test
    void canGetPlayer() {

        //given
        String name = "Player1";
        Player player = new Player();
        player.setId("kjdshfGI");
        player.setName(name);
        player.setAttributes(
                Map.of(
                        "strength", 85,
                        "speed", 92
                )
        );

        //when
        when(playerRepo.findByName(name)).thenReturn(player);
        PlayerBasicDTO  actual = playerService.getPlayer(name);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(name);
    }

    @Test
    void canNotGetPlayer() {

        //given
        String name = "Player1";
        Player player = new Player();

        //when
        when(playerRepo.findByName(name)).thenReturn(player);

        //then
        assertThrows(PlayerNotFoundException.class, () -> playerService.getPlayer(name));
    }

    @Test
    void canRegisterPlayers() {

        //given
        Player player1 = new Player();
        player1.setId("kjdshfGIkhvfytvf");
        player1.setName("Player1");
        player1.setAttributes(
                Map.of(
                        "strength", 85,
                        "speed", 92
                )
        );
        Player player2 = new Player();
        player2.setId("auebvgavbiu");
        player2.setName("Player2");
        player2.setAttributes(
                Map.of(
                        "strength",75 ,
                        "speed", 69
                )
        );
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        List<PlayerDTO> playersDTO = new ArrayList<>();
        playersDTO.add(new PlayerDTO(player1));
        playersDTO.add(new PlayerDTO(player2));

        //when
        when(playerRepo.saveAll(players)).thenReturn(players);
        String actual = playerService.registerPlayers(playersDTO);

        //then
        assertThat(actual).isEqualTo(PlayerConstants.SAVE_SUCCESS_MESSAGE);
    }

    @Test
    void canNotRegisterPlayers() {

        //given
        Player player1 = new Player();
        player1.setId("kjdshfGIkhvfytvf");
        player1.setName("Player1");
        player1.setAttributes(
                Map.of(
                        "strength", 85,
                        "speed", 92
                )
        );
        Player player2 = new Player();
        player2.setId("auebvgavbiu");
        player2.setName("Player2");
        player2.setAttributes(
                Map.of(
                        "strength",75 ,
                        "speed", 69
                )
        );
        List<Player> players = new ArrayList<>();
        players.add(player2);
        List<PlayerDTO> playersDTO = new ArrayList<>();
        playersDTO.add(new PlayerDTO(player1));
        playersDTO.add(new PlayerDTO(player2));

        //when
        when(playerRepo.findByName(player1.getName())).thenReturn(player1);
        String actual = playerService.registerPlayers(playersDTO);

        //then
        assertThat(actual).isEqualTo(PlayerConstants.SAVE_FAILURE_MESSAGE + "[" + player1.getName() + "]"
                + " as players with the same name already exists");
        verify(playerRepo, times(1)).saveAll(anyList());
    }

    @Test
    void canUpdatePlayers() {

        //given
        Player player1 = new Player();
        player1.setId("kjdshfGIkhvfytvf");
        player1.setName("Player1");
        player1.setAttributes(
                new HashMap<>(Map.of(
                        "strength", 85,
                        "speed", 92
                ))
        );
        Player player2 = new Player();
        player2.setId("auebvgavbiu");
        player2.setName("Player2");
        player2.setAttributes(
                new HashMap<>(Map.of(
                        "strength",75 ,
                        "speed", 69
                ))
        );
        List<PlayerDTO> playersDTO = new ArrayList<>();
        PlayerDTO playersDTO1 = new PlayerDTO();
        playersDTO1.setId(player1.getId());
        playersDTO1.setAttributes(
                        new HashMap<>(Map.of(
                                "strength", 96,
                                "speed", 78
                        ))
        );
        PlayerDTO playersDTO2 = new PlayerDTO();
        playersDTO2.setId(player2.getId());
        playersDTO2.setName("Player2updated");
        playersDTO.add(playersDTO1);
        playersDTO.add(playersDTO2);

        //when
        when(playerRepo.findById(player1.getId())).thenReturn(Optional.of(player1));
        when(playerRepo.findById(player2.getId())).thenReturn(Optional.of(player2));
        String actual = playerService.updatePlayers(playersDTO);

        //then
        assertThat(actual).isEqualTo(PlayerConstants.UPDATE_SUCCESS_MESSAGE);
        verify(playerRepo, times(1)).saveAll(anyList());
    }

    @Test
    void canNotUpdatePlayers() {

        //given
        Player player1 = new Player();
        player1.setId("kjdshfGIkhvfytvf");
        player1.setName("Player1");
        player1.setAttributes(
                new HashMap<>(Map.of(
                        "strength", 85,
                        "speed", 92
                ))
        );
        Player player2 = new Player();
        player2.setId("auebvgavbiu");
        player2.setName("Player2");
        player2.setAttributes(
                Map.of(
                        "strength",75 ,
                        "speed", 69
                )
        );
        List<PlayerDTO> playersDTO = new ArrayList<>();
        PlayerDTO playersDTO1 = new PlayerDTO();
        playersDTO1.setId(player1.getId());
        playersDTO1.setAttributes(
                new HashMap<>(Map.of(
                        "strength", 96,
                        "speed", 78
                ))
        );
        playersDTO.add(playersDTO1);
        playersDTO.add(new PlayerDTO(player2));


        //when
        when(playerRepo.findById(player1.getId())).thenReturn(Optional.of(player1));
        when(playerRepo.findById(player2.getId())).thenReturn(Optional.empty());
        String actual = playerService.updatePlayers(playersDTO);

        //then
        assertThat(actual).isEqualTo(PlayerConstants.UPDATE_FAILURE_MESSAGE +  player2.getId());
        verify(playerRepo, times(1)).saveAll(anyList());
    }
}