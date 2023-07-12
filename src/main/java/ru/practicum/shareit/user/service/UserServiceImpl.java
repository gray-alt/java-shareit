package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.mapToUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        if (userRepository.existsByEmailAndIdNot(userDto.getEmail(), userId)) {
            throw new AlreadyExistException("Пользователь уже существует с email " + userDto.getEmail());
        }
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + userId));
        User user = userRepository.save(foundUser.withUser(UserMapper.mapToUser(userDto)));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найден пользователь с id " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<User> users = userRepository.findAll();
        return UserMapper.mapToUserDto(users);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + userId));
        return UserMapper.mapToUserDto(user);
    }
}
