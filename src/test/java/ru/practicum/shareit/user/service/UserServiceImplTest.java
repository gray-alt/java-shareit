package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@AutoConfigureTestDatabase
public class UserServiceImplTest {
    private final UserService userService;

    @Test
    void addUser() {
        UserDto userDto = makeUserDto("test", "test@test.ru");
        UserDto newUserDto = userService.addUser(userDto);
        assertThat(newUserDto.getName(), equalTo("test"));
        assertThat(newUserDto.getEmail(), equalTo("test@test.ru"));
    }

    @Test
    void updateUserWithDoubleEmail() {
        UserDto userDto_1 = makeUserDto("test_1", "test_1@test.ru");
        UserDto newUserDto_1 = userService.addUser(userDto_1);

        UserDto userDto_2 = makeUserDto("test_2", "test_2@test.ru");
        UserDto newUserDto_2 = userService.addUser(userDto_2);

        UserDto userDtoForUpdate = makeUserDto("test_1", "test_2@test.ru");
        Assertions.assertThrows(AlreadyExistException.class,
                () -> userService.updateUser(newUserDto_1.getId(), userDtoForUpdate));
    }

    @Test
    void updateUserWithWrongId() {
        UserDto userDtoForUpdate = makeUserDto("test_2", "test_2@test.ru");
        Assertions.assertThrows(NotFoundException.class,
                () -> userService.updateUser(999L, userDtoForUpdate));
    }

    @Test
    void updateUserOnlyName() {
        UserDto userDto_1 = makeUserDto("test_1", "test_1@test.ru");
        UserDto newUserDto_1 = userService.addUser(userDto_1);

        UserDto userDtoForUpdate = makeUserDto("test_2", null);
        UserDto updatedUserDto = userService.updateUser(newUserDto_1.getId(), userDtoForUpdate);

        assertThat(newUserDto_1.getName(), equalTo("test_1"));
        assertThat(updatedUserDto.getName(), equalTo("test_2"));
    }

    @Test
    void updateUserOnlyEmail() {
        UserDto userDto_1 = makeUserDto("test_1", "test_1@test.ru");
        UserDto newUserDto_1 = userService.addUser(userDto_1);

        UserDto userDtoForUpdate = makeUserDto(null, "test_2@test.ru");
        UserDto updatedUserDto = userService.updateUser(newUserDto_1.getId(), userDtoForUpdate);

        assertThat(newUserDto_1.getEmail(), equalTo("test_1@test.ru"));
        assertThat(updatedUserDto.getEmail(), equalTo("test_2@test.ru"));
    }

    @Test
    void deleteUserWithWrongId() {
        Assertions.assertThrows(NotFoundException.class,
                () -> userService.deleteUser(999L));
    }

    @Test
    void deleteUser() {
        UserDto userDto = makeUserDto("test", "test@test.ru");
        UserDto newUserDto = userService.addUser(userDto);

        userService.deleteUser(newUserDto.getId());

        Assertions.assertThrows(NotFoundException.class,
                () -> userService.deleteUser(newUserDto.getId()));
    }

    @Test
    void getAllUsersWithEmptyList() {
        Collection<UserDto> userDtos = userService.getAllUsers();
        assertThat(userDtos.size(), equalTo(0));
    }

    @Test
    void getAllUsers() {
        UserDto userDto_1 = makeUserDto("test_1", "test_1@test.ru");
        UserDto newUserDto_1 = userService.addUser(userDto_1);

        UserDto userDto_2 = makeUserDto("test_2", "test_2@test.ru");
        UserDto newUserDto_2 = userService.addUser(userDto_2);

        Collection<UserDto> userDtos = userService.getAllUsers();
        assertThat(userDtos.size(), equalTo(2));

        assertThat(userDtos, hasItem( allOf(
                hasProperty("id", equalTo(newUserDto_1.getId())),
                hasProperty("name", equalTo(newUserDto_1.getName())),
                hasProperty("email", equalTo(newUserDto_1.getEmail()))
        )));

        assertThat(userDtos, hasItem( allOf(
                hasProperty("id", equalTo(newUserDto_2.getId())),
                hasProperty("name", equalTo(newUserDto_2.getName())),
                hasProperty("email", equalTo(newUserDto_2.getEmail()))
        )));
    }

    @Test
    void getUserByIdWithWrongId() {
        Assertions.assertThrows(NotFoundException.class,
                () -> userService.deleteUser(999L));
    }

    @Test
    void getUserById() {
        UserDto userDto_1 = makeUserDto("test_1", "test_1@test.ru");
        UserDto newUserDto_1 = userService.addUser(userDto_1);

        UserDto foundUserDto = userService.getUserById(newUserDto_1.getId());
        assertThat(foundUserDto, allOf(
                hasProperty("id", equalTo(newUserDto_1.getId())),
                hasProperty("name", equalTo(newUserDto_1.getName())),
                hasProperty("email", equalTo(newUserDto_1.getEmail()))
        ));
    }

    private UserDto makeUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }
}
