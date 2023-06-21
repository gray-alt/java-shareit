package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(@Qualifier("userServiceImpl") UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public Optional<UserDto> addUser(@Valid @RequestBody UserDto userDto) {
        Optional<User> optionalUser = userService.addUser(UserMapper.toUser(userDto));
        return Optional.of(UserMapper.toUserDto(optionalUser.orElseThrow()));
    }

    @PatchMapping("/{userId}")
    public Optional<UserDto> updateUser(@PathVariable Long userId,
                                        @RequestBody UserDto userDto) {
        Optional<User> optionalUser = userService.updateUser(userId, UserMapper.toUser(userDto));
        return Optional.of(UserMapper.toUserDto(optionalUser.orElseThrow()));
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        Collection<User> users = userService.getAllUsers();
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public Optional<UserDto> getUserById(@PathVariable Long userId) {
        Optional<User> optionalUser = userService.getUserById(userId);
        return Optional.of(UserMapper.toUserDto(optionalUser.orElseThrow()));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
