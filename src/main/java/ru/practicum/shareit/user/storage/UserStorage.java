package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Optional<User> addUser(User user);

    Optional<User> updateUser(Long userId, User user);

    void deleteUser(Long userId);

    Collection<User> getAllUsers();

    Optional<User> getUserById(Long userId);

    Optional<User> getUserByEmail(String email);

    boolean isUserExist(Long userId);

    boolean isUserNotExist(Long userId);

    boolean isUserExistByMail(String email);
}
