package com.learn.matchmaking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.matchmaking.constant.MatchConstants;
import com.learn.matchmaking.dto.MatchRequest;
import com.learn.matchmaking.dto.MatchResponse;
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
class MatchControllerIT {

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
    public MatchControllerIT(PlayerRepository playerRepository, TestRestTemplate restTemplate,
                             UserRepository userRepository, JWTService jwtService , ObjectMapper objectMapper
    ) {

        this.playerRepository = playerRepository;
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {

        baseUrl = "http://localhost:" + port + "/match/";
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
        player1.setIsSearchingForMatch(true);

        Player player2 = new Player();
        player2.setName("Player2");
        player2.setAttributes(new HashMap<>(Map.of("strength", 70, "speed", 85)));
        player2.setIsSearchingForMatch(true);

        Player player3 = new Player();
        player3.setName("Player3");
        player3.setAttributes(new HashMap<>(Map.of("strength", 85, "speed", 96)));
        player3.setIsSearchingForMatch(true);

        Player player4 = new Player();
        player4.setName("Player4");
        player4.setAttributes(new HashMap<>(Map.of("strength", 68, "speed", 54)));
        player4.setIsSearchingForMatch(true);

        Player player5 = new Player();
        player5.setName("Player5");
        player5.setAttributes(new HashMap<>(Map.of("strength", 62, "speed", 65)));
        player5.setIsSearchingForMatch(true);

        Player player6 = new Player();
        player6.setName("Player6");
        player6.setAttributes(new HashMap<>(Map.of("strength", 54, "speed", 43)));
        player6.setIsSearchingForMatch(true);

        return new ArrayList<>(List.of(player1, player2, player3, player4, player5, player6));
    }

    public Users createMockUser() {

        Users user =  new Users();
        user.setUsername("mockAdmin");
        user.setPassword(new BCryptPasswordEncoder(12).encode("Admin@000"));

        return user;
    }

    @Test
    void canMatchGroupFromPool() throws Exception {

        MatchRequest request = getRequestWithoutIds();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);
        ResponseEntity<MatchResponse> response = restTemplate.exchange(
                  baseUrl + "pool",
                  HttpMethod.POST,
                  entity,
                  MatchResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getGroups().size()).isEqualTo(3);
        assertThat(response.getBody().getMessage()).isEqualTo(MatchConstants.MATCH_SUCCESSFUL_MESSAGE);
    }

    @Test
    void canNotMatchGroupFromPool() throws Exception {

        List<String> playerNames = List.of("Player1", "Player2", "Player4", "Player5", "Player6");

        for(String playerName : playerNames) {

            Player player = playerRepository.findByName(playerName).get();
            player.setIsSearchingForMatch(false);
            playerRepository.save(player);
        }

        MatchRequest request = getRequestWithoutIds();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);
        ResponseEntity<MatchResponse> response = restTemplate.exchange(
                baseUrl + "pool",
                HttpMethod.POST,
                entity,
                MatchResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getGroups().size()).isEqualTo(0);
        assertThat(response.getBody().getMessage()).isEqualTo(MatchConstants.MATCH_MAKING_CRITERIA_MESSAGE);
    }

    @Test
    void canMatchGroupFromGivenIds() throws Exception {

        MatchRequest request = this.getRequestWithIds();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);
        ResponseEntity<MatchResponse> response = restTemplate.exchange(
                baseUrl + "custom",
                HttpMethod.POST,
                entity,
                MatchResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getGroups().size()).isEqualTo(3);
        assertThat(response.getBody().getMessage()).isEqualTo(MatchConstants.MATCH_SUCCESSFUL_MESSAGE);
    }

    @Test
    void canNotMatchGroupFromGivenIdsWhenPlayerIdsEmpty() throws Exception {

        MatchRequest request = getRequestWithoutIds();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);
        ResponseEntity<MatchResponse> response = restTemplate.exchange(
                baseUrl + "custom",
                HttpMethod.POST,
                entity,
                MatchResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getGroups().size()).isEqualTo(0);
        assertThat(response.getBody().getMessage()).isEqualTo(MatchConstants.MATCH_PLAYER_IDS_MANDATORY_MESSAGE);
    }

    @Test
    void canNotMatchGroupFromGivenIdsWhenPlayerNotFound() throws Exception {

        MatchRequest request = this.getRequestWithIds();
        request.getPlayerIds().add("gidhainghanhg7");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);
        ResponseEntity<MatchResponse> response = restTemplate.exchange(
                baseUrl + "custom",
                HttpMethod.POST,
                entity,
                MatchResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getGroups().size()).isEqualTo(0);
        assertThat(response.getBody().getMessage()).isEqualTo("Player with ID gidhainghanhg7 not found");
    }

    private static MatchRequest getRequestWithoutIds() {
        MatchRequest request = new MatchRequest();
        request.setGroupSize(2);
        request.setTargetAttributes(
                new HashMap<>(
                        Map.of(
                                "strength", 50,
                                "speed", 40
                        )
                )
        );
        request.setAttributeWeights(
                new HashMap<>(
                        Map.of(
                                "strength", 0.6,
                                "speed", 0.4
                        )
                )
        );
        return request;
    }

    private MatchRequest getRequestWithIds() {

        List<String> playerNames = List.of("Player1", "Player2", "Player3", "Player4", "Player5", "Player6");
        List<String> ids = new ArrayList<>();

        for(String playerName : playerNames) {

            Player player = playerRepository.findByName(playerName).get();
            ids.add(player.getId());
        }

        MatchRequest request = new MatchRequest();
        request.setPlayerIds(ids);
        request.setGroupSize(2);
        request.setTargetAttributes(
                new HashMap<>(
                        Map.of(
                                "strength", 50,
                                "speed", 40
                        )
                )
        );
        request.setAttributeWeights(
                new HashMap<>(
                        Map.of(
                                "strength", 0.6,
                                "speed", 0.4
                        )
                )
        );
        return request;
    }
}