package com.learn.matchmaking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.matchmaking.constant.PlayerConstants;
import com.learn.matchmaking.dto.PlayerBasicDTO;
import com.learn.matchmaking.dto.PlayerDTO;
import com.learn.matchmaking.model.Player;
import com.learn.matchmaking.model.Users;
import com.learn.matchmaking.repo.PlayerRepository;
import com.learn.matchmaking.repo.UserRepository;
import com.learn.matchmaking.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
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
class PlayerControllerIT {

    @LocalServerPort
    private int port;
    private String baseUrl;
    private String token;
    private final TestRestTemplate restTemplate;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final JWTService jwtService;
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
    public PlayerControllerIT(TestRestTemplate restTemplate, PlayerRepository playerRepository,
                              UserRepository userRepository, JWTService jwtService, ObjectMapper objectMapper) {

        this.restTemplate = restTemplate;
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
     void setUp() {

        baseUrl = "http://localhost:" + port + "/players/";
        playerRepository.deleteAll();
        playerRepository.saveAll(createMockPlayers());
        userRepository.deleteAll();
        userRepository.save(createMockUser());
        token = jwtService.generateToken("mockAdmin");
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

    public Users createMockUser() {

        Users user =  new Users();
        user.setUsername("mockAdmin");
        user.setPassword(new BCryptPasswordEncoder(12).encode("Admin@000"));

        return user;
    }

    @Test
    void canGetPlayers() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<PlayerBasicDTO>> response = restTemplate.exchange(
                baseUrl + "all",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(2);
    }

    @Test
    void canNotGetPlayers() {

        playerRepository.deleteAll();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<PlayerBasicDTO>> response = restTemplate.exchange(
                baseUrl + "all",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void canGetPlayer() {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<PlayerBasicDTO> response = restTemplate.exchange(
                baseUrl + "Player1",
                HttpMethod.GET,
                entity,
                PlayerBasicDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Player1");
    }

    @Test
    void canNotGetPlayer() {

        playerRepository.deleteAll();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<PlayerBasicDTO> response = restTemplate.exchange(
                baseUrl + "Player1",
                HttpMethod.GET,
                entity,
                PlayerBasicDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void canRegisterPlayers() throws Exception{

        PlayerDTO player3= new PlayerDTO();
        player3.setName("Player3");
        player3.setAttributes(new HashMap<>(Map.of("strength", 88, "speed", 98)));
        PlayerDTO player4= new PlayerDTO();
        player4.setName("Player4");
        player4.setAttributes(new HashMap<>(Map.of("strength", 81, "speed", 100)));
        List<PlayerDTO> players = List.of(player3, player4);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(players), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "register",
                 HttpMethod.POST,
                 entity,
                 String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(PlayerConstants.SAVE_SUCCESS_MESSAGE);
    }

    @Test
    void canNotRegisterPlayers() throws Exception {

        PlayerDTO player3= new PlayerDTO();
        player3.setName("Player1");
        player3.setAttributes(new HashMap<>(Map.of("strength", 88, "speed", 98)));
        PlayerDTO player4= new PlayerDTO();
        player4.setName("Player2");
        player4.setAttributes(new HashMap<>(Map.of("strength", 81, "speed", 100)));
        List<PlayerDTO> players = List.of(player3, player4);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(players), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "register",
                HttpMethod.POST,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(PlayerConstants.SAVE_FAILURE_MESSAGE
                + "[" + player3.getName() + ", " + player4.getName() + "]" + " as players with the same name already exists");
    }

    @Test
    void canUpdatePlayers() throws Exception {

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
        headers.setBearerAuth(token);
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
        headers.setBearerAuth(token);
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
        headers.setBearerAuth(token);
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