package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component("inMemoryUserStorage")
@RequiredArgsConstructor
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users;
    private final Map<String, User> usersByEmail;
    private long lastId;

    @Override
    public Optional<User> addUser(User user) {
        User newUser = User.builder()
                .id(++lastId)
                .name(user.getName())
                .email(user.getEmail())
                .build();

        users.put(newUser.getId(), newUser);
        usersByEmail.put(newUser.getEmail(), newUser);
        log.info("Добавлен пользователь " + newUser.getName() + " с id " + newUser.getId());
        return Optional.of(newUser);
    }

    @Override
    public Optional<User> updateUser(Long userId, User user) {
        User foundUser = users.get(userId);
        if (foundUser == null) {
            return Optional.empty();
        }

        usersByEmail.remove(foundUser.getEmail());

        User newUser = User.builder()
                .id(userId)
                .name(user.getName() != null ? user.getName() : foundUser.getName())
                .email(user.getEmail() != null ? user.getEmail() : foundUser.getEmail())
                .build();

        users.put(userId, newUser);
        usersByEmail.put(newUser.getEmail(), newUser);
        log.info("Обновлен пользователь " + newUser.getName() + " с id " + newUser.getId());
        return Optional.of(newUser);
    }

    @Override
    public void deleteUser(Long userId) {
        User foundUser = users.get(userId);
        if (foundUser != null) {
            log.info("Удален пользователь " + foundUser.getName() + " с id " + userId);
            usersByEmail.remove(foundUser.getEmail());
            users.remove(userId);
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        if (users.containsKey(userId)) {
            return Optional.of(users.get(userId));
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        if (usersByEmail.containsKey(email)) {
            return Optional.of(usersByEmail.get(email));
        }
        return Optional.empty();
    }

    @Override
    public boolean isExist(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean isNotExist(Long userId) {
        return !users.containsKey(userId);
    }

    @Override
    public boolean isExistByMail(String email) {
        return usersByEmail.containsKey(email);
    }
}
