package com.learn.matchmaking.service;

import com.learn.matchmaking.model.MyUserDetails;
import com.learn.matchmaking.model.Users;
import com.learn.matchmaking.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private UserRepository userRepo;

    @Autowired
    public MyUserDetailsService(UserRepository userRepo) { this.userRepo = userRepo; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        return new MyUserDetails(user);
    }
}
