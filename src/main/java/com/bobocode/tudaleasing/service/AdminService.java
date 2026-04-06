package com.bobocode.tudaleasing.service;

import com.bobocode.tudaleasing.dto.CreateUserRequestDto;
import com.bobocode.tudaleasing.dto.UserResponseDto;
import com.bobocode.tudaleasing.entity.User;
import com.bobocode.tudaleasing.mapper.UserMapper;
import com.bobocode.tudaleasing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    String.format("User with email '%s' already exists.", request.email())
            );
        }

        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    String.format("User with ID %d not found.", id)
            );
        }
        userRepository.deleteById(id);
    }
}