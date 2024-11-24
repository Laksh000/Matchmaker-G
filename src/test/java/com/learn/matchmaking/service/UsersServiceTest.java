package com.learn.matchmaking.service;

import com.learn.matchmaking.constant.UserConstants;
import com.learn.matchmaking.model.Users;
import com.learn.matchmaking.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private JWTService jwtService;
    @InjectMocks private UsersService usersService;

    private Users testAdmin;

    @BeforeEach
    void setUp() {

        testAdmin = createMockUser();
    }

    private Users createMockUser() {

        Users user = new Users();
        user.setUsername("testAdmin");
        user.setPassword("Admin@000");

        return user;
    }

    @Test
    void canRegisterUser() {

        when(userRepository.findByUsername(testAdmin.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(testAdmin)).thenReturn(testAdmin);
        String response = usersService.registerUser(testAdmin);

        assertThat(response).isEqualTo(UserConstants.USER_REGISTRATION_SUCCESSFUL_MESSAGE);
    }

    @Test
    void canNotRegisterUser() {

        when(userRepository.findByUsername(testAdmin.getUsername())).thenReturn(Optional.of(testAdmin));
        String response = usersService.registerUser(testAdmin);

        assertThat(response).isEqualTo(String.format(UserConstants.USER_REGISTRATION_FAILED_MESSAGE, testAdmin.getUsername()));
    }

    @Test
    void canVerifyUser() {

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                testAdmin.getUsername(), testAdmin.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("USER"))
        );

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(testAdmin.getUsername(), testAdmin.getPassword())
        )).thenReturn(authentication);
        when(jwtService.generateToken(testAdmin.getUsername())).thenReturn("dummytesttoken");
        String response = usersService.verifyUser(testAdmin);

        assertThat(response).isEqualTo("dummytesttoken");
    }

    @Test
    void canNotVerifyUser() {

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                testAdmin.getUsername(), testAdmin.getPassword()
        );
        authentication.setAuthenticated(false);

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(testAdmin.getUsername(), testAdmin.getPassword())
        )).thenReturn(authentication);

        assertThrows(UsernameNotFoundException.class ,() -> usersService.verifyUser(testAdmin));
    }
}