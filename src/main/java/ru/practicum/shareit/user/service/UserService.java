package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    User addUser(User user);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);

    Collection<User> getAllUsers();

    User getUserById(Long userId);
}
