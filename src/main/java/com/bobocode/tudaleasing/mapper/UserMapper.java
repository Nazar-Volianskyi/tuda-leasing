package com.bobocode.tudaleasing.mapper;

import com.bobocode.tudaleasing.dto.CreateUserRequestDto;
import com.bobocode.tudaleasing.dto.UserResponseDto;
import com.bobocode.tudaleasing.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponseDto toDto(User user);

    User toEntity(CreateUserRequestDto request);
}