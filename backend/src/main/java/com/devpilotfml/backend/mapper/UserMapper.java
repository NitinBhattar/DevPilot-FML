package com.devpilotfml.backend.mapper;

import com.devpilotfml.backend.dto.UserResponseDto;
import com.devpilotfml.backend.entity.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    public UserResponseDto toDto(User user);
}
