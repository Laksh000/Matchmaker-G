package com.learn.matchmaking.service;

import com.learn.matchmaking.model.Users;
import com.learn.matchmaking.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private MyUserDetailsService myUserDetailsService;

    @Test
    void canLoadUserByUsername() {

        Users user = new Users();
        user.setUsername("testAdmin");
        user.setPassword("Admin@000");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UserDetails response = myUserDetailsService.loadUserByUsername(user.getUsername());

        assertThat(response.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void canNotLoadUserByUsername() {

        when(userRepository.findByUsername("testAdmin")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> myUserDetailsService.loadUserByUsername("testAdmin"));
    }
}