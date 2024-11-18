package com.learn.matchmaking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.matchmaking.constant.PlayerConstants;
import com.learn.matchmaking.dto.PlayerBasicDTO;
import com.learn.matchmaking.dto.PlayerDTO;
import com.learn.matchmaking.model.Player;
import com.learn.matchmaking.repo.PlayerRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlayerControllerIntTest {

    @LocalServerPort
    private int port;
    private String baseUrl;
    private final TestRestTemplate restTemplate;
    private final PlayerRepository playerRepository;
    private final ObjectMapper objectMapper;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5.0.1"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "integrationTest");
        registry.add("spring.data.mongodb.auto-index-creation", () -> true);
    }

    @Autowired
    public PlayerControllerIntTest(PlayerRepository playerRepository, TestRestTemplate restTemplate, ObjectMapper objectMapper) {

        this.playerRepository = playerRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
     void setUp() {

        baseUrl = "http://localhost:" + port + "/players/";
        playerRepository.deleteAll();
        playerRepository.saveAll(createMockPlayers());
    }

    public List<Player> createMockPlayers() {

        Player player1 = new Player();
        player1.setName("Player1");
        player1.setAttributes(new HashMap<>(Map.of("strength", 80, "speed", 90)));

        Player player2 = new Player();
        player2.setName("Player2");
        player2.setAttributes(new HashMap<>(Map.of("strength", 70, "speed", 85)));

        return new ArrayList<>(List.of(player1, player2));
    }

    @Test
    void canGetPlayers() {

        ResponseEntity<List<PlayerBasicDTO>> response = restTemplate.exchange(
                baseUrl + "all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(2);
    }

    @Test
    void canNotGetPlayers() {

        playerRepository.deleteAll();
        ResponseEntity<List<PlayerBasicDTO>> response = restTemplate.exchange(
                baseUrl + "all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void canGetPlayer() {

        ResponseEntity<PlayerBasicDTO> response = restTemplate.exchange(
                baseUrl + "Player1",
                HttpMethod.GET,
                null,
                PlayerBasicDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Player1");
    }

    @Test
    void canNotGetPlayer() {

        playerRepository.deleteAll();
        ResponseEntity<PlayerBasicDTO> response = restTemplate.exchange(
                baseUrl + "Player1",
                HttpMethod.GET,
                null,
                PlayerBasicDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void canRegisterPlayers() {

        PlayerDTO player3= new PlayerDTO();
        player3.setName("Player3");
        player3.setAttributes(new HashMap<>(Map.of("strength", 88, "speed", 98)));
        PlayerDTO player4= new PlayerDTO();
        player4.setName("Player4");
        player4.setAttributes(new HashMap<>(Map.of("strength", 81, "speed", 100)));
        List<PlayerDTO> players = List.of(player3, player4);

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "register",
                players,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(PlayerConstants.SAVE_SUCCESS_MESSAGE);
    }

    @Test
    void canNotRegisterPlayers() {

        PlayerDTO player3= new PlayerDTO();
        player3.setName("Player1");
        player3.setAttributes(new HashMap<>(Map.of("strength", 88, "speed", 98)));
        PlayerDTO player4= new PlayerDTO();
        player4.setName("Player2");
        player4.setAttributes(new HashMap<>(Map.of("strength", 81, "speed", 100)));
        List<PlayerDTO> players = List.of(player3, player4);

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "register",
                players,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(PlayerConstants.SAVE_FAILURE_MESSAGE
                + "[" + player3.getName() + ", " + player4.getName() + "]" + " as players with the same name already exists");
    }

    @Test
    void canUpdatePlayers() throws JsonProcessingException {

        String player1Id = playerRepository.findByName("Player1").get().getId();
        String player2Id = playerRepository.findByName("Player2").get().getId();
        PlayerDTO player1= new PlayerDTO();
        player1.setId(player1Id);
        player1.setAttributes(new HashMap<>(Map.of("strength", 88, "speed", 98)));
        PlayerDTO player2= new PlayerDTO();
        player2.setId(player2Id);
        player2.setAttributes(new HashMap<>(Map.of("strength", 81, "speed", 100)));
        List<PlayerDTO> players = List.of(player1, player2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(players), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "update",
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(PlayerConstants.UPDATE_SUCCESS_MESSAGE);
    }

    @Test
    void canNotUpdatePlayersWhenPlayerDoesNotExist() throws JsonProcessingException {

        String player1Id = playerRepository.findByName("Player1").get().getId();
        PlayerDTO player1= new PlayerDTO();
        player1.setId(player1Id);
        player1.setAttributes(new HashMap<>(Map.of("strength", 88, "speed", 98)));
        PlayerDTO player3= new PlayerDTO();
        player3.setId("player3id");
        player3.setAttributes(new HashMap<>(Map.of("strength", 81, "speed", 100)));
        List<PlayerDTO> players = List.of(player1, player3);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(players), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "update",
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(PlayerConstants.UPDATE_FAILURE_MESSAGE + player3.getId());
    }

    @Test
    void canNotUpdatePlayersWhenPlayerIdIsNull() throws JsonProcessingException {

        String player1Id = playerRepository.findByName("Player1").get().getId();
        PlayerDTO player1= new PlayerDTO();
        player1.setId(player1Id);
        player1.setAttributes(new HashMap<>(Map.of("strength", 88, "speed", 98)));
        PlayerDTO player3= new PlayerDTO();
        player3.setAttributes(new HashMap<>(Map.of("strength", 81, "speed", 100)));
        List<PlayerDTO> players = List.of(player1, player3);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(players), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "update",
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(String.format(PlayerConstants.UPDATE_FAILURE_MESSAGE2, 1));
    }
}