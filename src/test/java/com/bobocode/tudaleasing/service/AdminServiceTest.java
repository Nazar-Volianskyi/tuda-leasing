package com.bobocode.tudaleasing.service;

import com.bobocode.tudaleasing.dto.CreateUserRequestDto;
import com.bobocode.tudaleasing.dto.UserResponseDto;
import com.bobocode.tudaleasing.entity.User;
import com.bobocode.tudaleasing.entity.enums.Role;
import com.bobocode.tudaleasing.mapper.UserMapper;
import com.bobocode.tudaleasing.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService – unit tests")
class AdminServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserMapper userMapper;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks AdminService adminService;

    private User sampleUser;
    private UserResponseDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("test@example.com");
        sampleUser.setFirstName("John");
        sampleUser.setLastName("Doe");
        sampleUser.setRole(Role.MANAGER);

        sampleDto = new UserResponseDto(1L, "test@example.com", "John", "Doe", Role.MANAGER);
    }


    @Test
    @DisplayName("getAllUsers: returns list of user DTOs")
    void getAllUsers_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));
        when(userMapper.toDto(sampleUser)).thenReturn(sampleDto);

        List<UserResponseDto> result = adminService.getAllUsers();

        assertThat(result).containsExactly(sampleDto);
    }

    @Test
    @DisplayName("getAllUsers: returns empty list when no users")
    void getAllUsers_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDto> result = adminService.getAllUsers();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("createUser: creates and returns user DTO")
    void createUser_createsUser() {
        CreateUserRequestDto request = new CreateUserRequestDto(
                "new@example.com", "pass123", "Jane", "Doe", Role.MANAGER);

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(sampleUser);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded-pass");
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        when(userMapper.toDto(sampleUser)).thenReturn(sampleDto);

        UserResponseDto result = adminService.createUser(request);

        assertThat(result).isEqualTo(sampleDto);
        verify(passwordEncoder).encode("pass123");
        assertThat(sampleUser.getPassword()).isEqualTo("encoded-pass");
    }

    @Test
    @DisplayName("createUser: throws when email already exists")
    void createUser_throwsWhenEmailExists() {
        CreateUserRequestDto request = new CreateUserRequestDto(
                "existing@example.com", "pass123", "Jane", "Doe", Role.MANAGER);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> adminService.createUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("existing@example.com");
    }

    @Test
    @DisplayName("createUser: password gets encoded before saving")
    void createUser_encodesPassword() {
        CreateUserRequestDto request = new CreateUserRequestDto(
                "new2@example.com", "rawPassword", "Alice", "Smith", Role.ADMINISTRATOR);
        User userEntity = new User();
        when(userRepository.existsByEmail("new2@example.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode("rawPassword")).thenReturn("$2a$encoded");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toDto(any())).thenReturn(sampleDto);

        adminService.createUser(request);

        verify(passwordEncoder).encode("rawPassword");
        assertThat(userEntity.getPassword()).isEqualTo("$2a$encoded");
    }

    @Test
    @DisplayName("deleteUser: deletes user by id")
    void deleteUser_deletesUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        adminService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser: throws when user not found")
    void deleteUser_throwsWhenNotFound() {
        when(userRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> adminService.deleteUser(42L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("42");
    }
}

