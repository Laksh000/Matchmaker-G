package com.learn.matchmaking.controller;

import com.learn.matchmaking.constant.UserConstants;
import com.learn.matchmaking.model.Users;
import com.learn.matchmaking.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("auth")
@RestController
public class UserController {

    private UsersService usersService;

    @Autowired
    public UserController(UsersService usersService) { this.usersService = usersService; }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Users user) {

        String message = usersService.registerUser(user);

        if(message.equals(UserConstants.USER_REGISTRATION_SUCCESSFUL_MESSAGE)){

            return new ResponseEntity<>(message, HttpStatus.CREATED);

        } else {

            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody Users user) {

        try {

            return new ResponseEntity<>(usersService.verifyUser(user), HttpStatus.OK);

        } catch (Exception e ) {

            return new ResponseEntity<>(UserConstants.USER_LOGIN_FAILED_MESSAGE, HttpStatus.FORBIDDEN);
        }
    }
}
