package com.learn.matchmaking.filter;

import com.learn.matchmaking.service.JWTService;
import com.learn.matchmaking.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JWTFilterTest {

    @Mock private JWTService jwtService;
    @Mock private MyUserDetailsService userDetailsService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain chain;
    @Mock private UserDetails userDetails;
    @InjectMocks private JWTFilter jwtFilter;

    @BeforeEach
    void setUp() {

        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_validToken() throws ServletException, IOException {

        String token = "dummyToken";
        String username = "testAdmin";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, chain);

        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtService).validateToken(token, userDetails);
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_inValidToken() throws ServletException, IOException {

        String token = "invalidToken";
        String username = "testAdmin";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, chain);

        verify(jwtService).validateToken(token, userDetails);
        verify(chain).doFilter(request, response);
    }
}