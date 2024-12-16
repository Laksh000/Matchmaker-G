package com.learn.matchmaking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootApplication
public class MatchmakingApplication {

    public static void main(String[] args) {

        SpringApplication.run(MatchmakingApplication.class, args);
    }

}
