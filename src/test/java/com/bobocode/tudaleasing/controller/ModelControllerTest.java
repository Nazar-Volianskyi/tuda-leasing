package com.bobocode.tudaleasing.controller;

import com.bobocode.tudaleasing.dto.CreateUserRequestDto;
import com.bobocode.tudaleasing.dto.UserResponseDto;
import com.bobocode.tudaleasing.entity.enums.Role;
import com.bobocode.tudaleasing.security.JwtAuthenticationFilter;
import com.bobocode.tudaleasing.security.JwtUtil;
import com.bobocode.tudaleasing.security.UserDetailsServiceImpl;
import com.bobocode.tudaleasing.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminController – slice tests")
class ModelControllerTest {

    @Autowired MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean AdminService adminService;
    @MockitoBean CacheManager cacheManager;

    @MockitoBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean JwtUtil                 jwtUtil;
    @MockitoBean UserDetailsServiceImpl  userDetailsService;


    @Test
    @DisplayName("GET /admin/users – returns 200 with list of users")
    void getAllUsers_returns200WithList() throws Exception {
        UserResponseDto user1 = new UserResponseDto(1L, "alice@example.com", "Alice", "Smith",  Role.ADMINISTRATOR);
        UserResponseDto user2 = new UserResponseDto(2L, "bob@example.com",   "Bob",   "Jones",  Role.MANAGER);

        when(adminService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("alice@example.com"))
                .andExpect(jsonPath("$[0].role").value("ADMINISTRATOR"))
                .andExpect(jsonPath("$[1].email").value("bob@example.com"))
                .andExpect(jsonPath("$[1].role").value("MANAGER"));
    }

    @Test
    @DisplayName("GET /admin/users – returns 200 with empty list when no users exist")
    void getAllUsers_returnsEmptyList() throws Exception {
        when(adminService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    @DisplayName("POST /admin/users – valid request returns 201 with created user")
    void createUser_validRequest_returns201() throws Exception {
        CreateUserRequestDto request = new CreateUserRequestDto(
                "new@example.com", "Secure@123", "New", "User", Role.MANAGER);
        UserResponseDto created = new UserResponseDto(3L, "new@example.com", "New", "User", Role.MANAGER);

        when(adminService.createUser(any(CreateUserRequestDto.class))).thenReturn(created);

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.role").value("MANAGER"));
    }

    @Test
    @DisplayName("POST /admin/users – blank email returns 400")
    void createUser_blankEmail_returns400() throws Exception {
        CreateUserRequestDto bad = new CreateUserRequestDto(
                "", "Pass@1", "A", "B", Role.MANAGER);

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(adminService);
    }

    @Test
    @DisplayName("POST /admin/users – invalid email format returns 400")
    void createUser_invalidEmail_returns400() throws Exception {
        CreateUserRequestDto bad = new CreateUserRequestDto(
                "not-an-email", "Pass@1", "A", "B", Role.MANAGER);

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(adminService);
    }

    @Test
    @DisplayName("POST /admin/users – blank password returns 400")
    void createUser_blankPassword_returns400() throws Exception {
        CreateUserRequestDto bad = new CreateUserRequestDto(
                "valid@example.com", "", "A", "B", Role.MANAGER);

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(adminService);
    }

    @Test
    @DisplayName("POST /admin/users – blank first name returns 400")
    void createUser_blankFirstName_returns400() throws Exception {
        CreateUserRequestDto bad = new CreateUserRequestDto(
                "valid@example.com", "Pass@1", "", "B", Role.MANAGER);

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(adminService);
    }

    @Test
    @DisplayName("POST /admin/users – null role returns 400")
    void createUser_nullRole_returns400() throws Exception {
        String json = """
                {"email":"valid@example.com","password":"Pass@1","firstName":"A","lastName":"B"}
                """;

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(adminService);
    }

    @Test
    @DisplayName("DELETE /admin/users/{id} – existing user returns 204 No Content")
    void deleteUser_existingId_returns204() throws Exception {
        doNothing().when(adminService).deleteUser(1L);

        mockMvc.perform(delete("/admin/users/1"))
                .andExpect(status().isNoContent());

        verify(adminService).deleteUser(1L);
    }

    @Test
    @DisplayName("DELETE /admin/users/{id} – throws when user not found")
    void deleteUser_notFound_throwsException() {
        doThrow(new IllegalArgumentException("User with ID 99 not found."))
                .when(adminService).deleteUser(99L);

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                mockMvc.perform(delete("/admin/users/99")).andReturn()
        ).hasMessageContaining("99");
    }

    @Test
    @DisplayName("DELETE /admin/users/{id} – service is called with the correct id")
    void deleteUser_callsServiceWithCorrectId() throws Exception {
        doNothing().when(adminService).deleteUser(eq(42L));

        mockMvc.perform(delete("/admin/users/42"))
                .andExpect(status().isNoContent());

        verify(adminService, times(1)).deleteUser(42L);
    }
}

