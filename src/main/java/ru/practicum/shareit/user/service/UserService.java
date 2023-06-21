package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {
    Optional<User> addUser(User user);

    Optional<User> updateUser(Long userId, User user);

    void deleteUser(Long userId);

    Collection<User> getAllUsers();

    Optional<User> getUserById(Long userId);
}
