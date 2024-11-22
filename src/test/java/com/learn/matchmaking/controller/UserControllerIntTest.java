package com.learn.matchmaking.controller;

import com.learn.matchmaking.constant.UserConstants;
import com.learn.matchmaking.model.Users;
import com.learn.matchmaking.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntTest {

    @LocalServerPort
    private int port;
    private String baseUrl;
    private String token;
    private final TestRestTemplate restTemplate;
    private final UserRepository userRepository;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5.0.1"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "integrationTest");
        registry.add("spring.data.mongodb.auto-index-creation", () -> true);
    }

    @Autowired
    public UserControllerIntTest(TestRestTemplate restTemplate, UserRepository userRepository) {

        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setUp() {

        baseUrl = "http://localhost:" + port + "/auth";
        userRepository.deleteAll();
    }

    @Test
    void canRegisterUser() {

        Users user = new Users();
        user.setUsername("mockAdmin");
        user.setPassword("Admin@000");

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/register",
                user,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(UserConstants.USER_REGISTRATION_SUCCESSFUL_MESSAGE);
    }

    @Test
    void canNotregisterUser() {

        Users user = new Users();
        user.setUsername("mockAdmin");
        user.setPassword(new BCryptPasswordEncoder(12).encode("Admin@000"));

        userRepository.save(user);

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/register",
                user,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(String.format(UserConstants.USER_REGISTRATION_FAILED_MESSAGE, user.getUsername()));
    }

    @Test
    void canLoginUser() {

        Users user = new Users();
        user.setUsername("mockAdmin");
        user.setPassword(new BCryptPasswordEncoder(12).encode("Admin@000"));

        userRepository.save(user);
        user.setPassword("Admin@000");

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/login",
                user,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void canNotLoginUser() {

        Users user = new Users();
        user.setUsername("mockAdmin");
        user.setPassword("Admin@000");

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/login",
                user,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(UserConstants.USER_LOGIN_FAILED_MESSAGE);
    }
}