package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User mapToUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static Collection<UserDto> mapToUserDto(Collection<User> users) {
        Collection<UserDto> userDtos = users.stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
        return userDtos;
    }
}
