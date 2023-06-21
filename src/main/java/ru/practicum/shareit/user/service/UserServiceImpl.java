package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public UserServiceImpl(@Qualifier("inMemoryUserStorage") UserStorage userStorage,
                           @Qualifier("inMemoryItemStorage") ItemStorage itemStorage) {
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    @Override
    public Optional<User> addUser(User user) {
        if (userStorage.isUserExistByMail(user.getEmail())) {
            throw new AlreadyExistException("Пользователь уже существует с email " + user.getEmail());
        }
        return userStorage.addUser(user);
    }

    @Override
    public Optional<User> updateUser(Long userId, User user) {
        Optional<User> foundUserById = userStorage.getUserById(userId);
        if (foundUserById.isEmpty()) {
            throw new NotFoundException("Не найден пользователь с id " + user.getId());
        }

        Optional<User> foundUserByMail = userStorage.getUserByEmail(user.getEmail());
        if (foundUserByMail.isPresent() && !foundUserByMail.equals(foundUserById)) {
            throw new AlreadyExistException("Пользователь уже существует с email " + user.getEmail());
        }
        return userStorage.updateUser(userId, user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (userStorage.isUserNotExist(userId)) {
            throw new NotFoundException("Не найден пользователь с id " + userId);
        }
        itemStorage.deleteAllOwnerItems(userId);
        userStorage.deleteUser(userId);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        Optional<User> optionalUser = userStorage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Не найден пользователь с id " + userId);
        }
        return optionalUser;
    }
}
