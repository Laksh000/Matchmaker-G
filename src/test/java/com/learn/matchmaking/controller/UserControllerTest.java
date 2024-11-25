package com.learn.matchmaking.controller;

import com.learn.matchmaking.constant.UserConstants;
import com.learn.matchmaking.dto.UsersDTO;
import com.learn.matchmaking.service.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private UsersService usersService;
    @InjectMocks private UserController userController;

    private UsersDTO createMockUser() {

        UsersDTO user = new UsersDTO();
        user.setUsername("testAdmin");
        user.setPassword("Admin@000");

        return user;
    }

    @Test
    void canRegisterUser() {

        UsersDTO user = createMockUser();

        when(usersService.registerUser(user)).thenReturn(UserConstants.USER_REGISTRATION_SUCCESSFUL_MESSAGE);

        ResponseEntity<String> response = userController.registerUser(user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(UserConstants.USER_REGISTRATION_SUCCESSFUL_MESSAGE);
    }

    @Test
    void canNotRegisterUser() {

        UsersDTO user = createMockUser();

        when(usersService.registerUser(user)).thenReturn(UserConstants.USER_REGISTRATION_FAILED_MESSAGE);

        ResponseEntity<String> response = userController.registerUser(user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(UserConstants.USER_REGISTRATION_FAILED_MESSAGE);
    }

    @Test
    void canLoginUser() {

        UsersDTO user = createMockUser();

        when(usersService.verifyUser(user)).thenReturn("dummyToken");

        ResponseEntity<String> response = userController.loginUser(user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("dummyToken");
    }

    @Test
    void canNotLoginUser() {

        UsersDTO user = createMockUser();

        when(usersService.verifyUser(user)).thenThrow(new UsernameNotFoundException(UserConstants.USER_LOGIN_FAILED_MESSAGE));

        ResponseEntity<String> response = userController.loginUser(user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(UserConstants.USER_LOGIN_FAILED_MESSAGE);
    }
}