package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);

    Collection<UserDto> getAllUsers();

    UserDto getUserById(Long userId);
}
