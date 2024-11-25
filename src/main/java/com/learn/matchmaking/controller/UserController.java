package com.learn.matchmaking.controller;

import com.learn.matchmaking.constant.UserConstants;
import com.learn.matchmaking.dto.UsersDTO;
import com.learn.matchmaking.model.Users;
import com.learn.matchmaking.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User(Game Admin) Registration and Authentication")
@RequestMapping("auth")
@RestController
public class UserController {

    private UsersService usersService;

    @Autowired
    public UserController(UsersService usersService) { this.usersService = usersService; }

    @Operation(summary = "Registration")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Registration Successful",
                    content = {@Content}
            ),
            @ApiResponse(
                    responseCode = "403", description = "Registration Failed",
                    content = {@Content}
            )
    })
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UsersDTO user) {

        String message = usersService.registerUser(user);

        if(message.equals(UserConstants.USER_REGISTRATION_SUCCESSFUL_MESSAGE)){

            return new ResponseEntity<>(message, HttpStatus.CREATED);

        } else {

            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get a JWT token")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Authentication Successful",
                    content = {@Content}
            ),
            @ApiResponse(
                    responseCode = "403", description = "Authentication Failed",
                    content = {@Content}
            )
    })
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UsersDTO user) {

        try {

            return new ResponseEntity<>(usersService.verifyUser(user), HttpStatus.OK);

        } catch (Exception e ) {

            return new ResponseEntity<>(UserConstants.USER_LOGIN_FAILED_MESSAGE, HttpStatus.FORBIDDEN);
        }
    }
}
