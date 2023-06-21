package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InMemoryUserStorageTest {
    private final InMemoryUserStorage userStorage;

    @Test
    public void testAddUser() {
        User newUser = User.builder()
                .name("New user")
                .email("user email")
                .build();

        Optional<User> userOptional = userStorage.addUser(newUser);

        assertThat(userOptional)
                .isPresent();
    }

    @Test
    public void testUpdateUser() {
        User newUser = User.builder()
                .name("New user")
                .email("user email")
                .build();

        Optional<User> userOptional = userStorage.addUser(newUser);

        assertThat(userOptional)
                .isPresent();

        newUser = userOptional.get();

        User userForUpdate = User.builder()
                .name("New user update")
                .email("user email")
                .build();

        userOptional = userStorage.updateUser(newUser.getId(), userForUpdate);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "New user update")
                );
    }

    @Test
    public void testUpdateUserWithWrongId() {
        User userForUpdate = User.builder()
                .name("New user update")
                .email("user email")
                .build();

        Optional<User> userOptional = userStorage.updateUser(999L, userForUpdate);

        assertThat(userOptional)
                .isEmpty();
    }

    @Test
    public void testDeleteUser() {
        User newUser = User.builder()
                .name("New user")
                .email("user email")
                .build();

        Optional<User> userOptional = userStorage.addUser(newUser);

        assertThat(userOptional)
                .isPresent();

        userStorage.deleteUser(userOptional.get().getId());

        Optional<User> getUserOptional = userStorage.getUserById(userOptional.get().getId());

        assertThat(getUserOptional)
                .isEmpty();
    }

    @Test
    public void testDeleteUserWithWrongId() {
        User newUser = User.builder()
                .name("New user")
                .email("user email")
                .build();

        Optional<User> userOptional = userStorage.addUser(newUser);

        assertThat(userOptional)
                .isPresent();

        userStorage.deleteUser(999L);

        Optional<User> getUserOptional = userStorage.getUserById(userOptional.get().getId());

        assertThat(getUserOptional)
                .isPresent();
    }

    @Test
    public void testGetUser() {
        User newUser = User.builder()
                .name("New user")
                .email("user email")
                .build();

        Optional<User> userOptional = userStorage.addUser(newUser);

        assertThat(userOptional)
                .isPresent();

        Optional<User> getUserOptional = userStorage.getUserById(userOptional.get().getId());

        assertThat(getUserOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", userOptional.get().getId())
                );
    }

    @Test
    public void testGetUserWithWrongId() {
        Optional<User> getUserOptional = userStorage.getUserById(999L);

        assertThat(getUserOptional)
                .isEmpty();
    }

    @Test
    public void testGetUserByEmail() {
        User newUser = User.builder()
                .name("New user")
                .email("user@email.ru")
                .build();

        Optional<User> userOptional = userStorage.addUser(newUser);

        assertThat(userOptional)
                .isPresent();

        Optional<User> getUserOptional = userStorage.getUserByEmail("user@email.ru");

        assertThat(getUserOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", userOptional.get().getId())
                );
    }

    @Test
    public void testGetUserByEmailWithWrongEmail() {
        Optional<User> getUserOptional = userStorage.getUserByEmail("xxx@xxx.xx");

        assertThat(getUserOptional)
                .isEmpty();
    }

    @Test
    public void testGetAllUsers() {
        Collection<User> users = userStorage.getAllUsers();

        User newUser = User.builder()
                .name("New user")
                .email("user email")
                .build();

        userStorage.addUser(newUser);

        Collection<User> usersPlusOne = userStorage.getAllUsers();

        assertThat(usersPlusOne)
                .size()
                .isEqualTo(users.size() + 1);
    }
}
