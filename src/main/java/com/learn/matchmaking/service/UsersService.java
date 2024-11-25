package com.learn.matchmaking.service;

import com.learn.matchmaking.constant.UserConstants;
import com.learn.matchmaking.dto.UsersDTO;
import com.learn.matchmaking.model.Users;
import com.learn.matchmaking.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepo;
    private final JWTService jwtService;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    public UsersService(UserRepository userRepo, AuthenticationManager authenticationManager, JWTService jwtService) {

        this.userRepo = userRepo;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public String registerUser(UsersDTO userDTO) {

        Optional<Users> existingUser = userRepo.findByUsername(userDTO.getUsername());

        if (existingUser.isEmpty()) {

            Users user = new Users(userDTO);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepo.save(user);

            return UserConstants.USER_REGISTRATION_SUCCESSFUL_MESSAGE;
        } else {

            return String.format(UserConstants.USER_REGISTRATION_FAILED_MESSAGE, existingUser.get().getUsername());
        }
    }

    public String verifyUser(UsersDTO user) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()
                ));

        if (authentication.isAuthenticated()) {

            return jwtService.generateToken(user.getUsername());
        } else {

            throw new UsernameNotFoundException(UserConstants.USER_LOGIN_FAILED_MESSAGE);
        }
    }

}
