package com.bobocode.tudaleasing.controller;

import com.bobocode.tudaleasing.dto.AuthRequestDto;
import com.bobocode.tudaleasing.security.JwtAuthenticationFilter;
import com.bobocode.tudaleasing.security.JwtUtil;
import com.bobocode.tudaleasing.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController – slice tests")
class AuthControllerTest {

    @Autowired MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean AuthenticationManager authenticationManager;
    @MockitoBean JwtUtil                jwtUtil;
    @MockitoBean CacheManager           cacheManager;

    @MockitoBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean UserDetailsServiceImpl  userDetailsService;


    @Test
    @DisplayName("POST /auth/login – valid credentials return 200 with token")
    void login_validCredentials_returns200() throws Exception {
        User mockUser = new User("admin@example.com", "encoded-pass",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR")));

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authToken);
        when(jwtUtil.generateToken(mockUser)).thenReturn("jwt-token-value");

        AuthRequestDto request = new AuthRequestDto("admin@example.com", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-value"))
                .andExpect(jsonPath("$.email").value("admin@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMINISTRATOR"));
    }

    @Test
    @DisplayName("POST /auth/login – invalid credentials return 401")
    void login_invalidCredentials_returns401() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        AuthRequestDto request = new AuthRequestDto("wrong@example.com", "wrongpass");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /auth/login – blank email returns 400")
    void login_blankEmail_returns400() throws Exception {
        AuthRequestDto request = new AuthRequestDto("", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login – invalid email format returns 400")
    void login_invalidEmailFormat_returns400() throws Exception {
        AuthRequestDto request = new AuthRequestDto("not-an-email", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login – blank password returns 400")
    void login_blankPassword_returns400() throws Exception {
        AuthRequestDto request = new AuthRequestDto("admin@example.com", "");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
