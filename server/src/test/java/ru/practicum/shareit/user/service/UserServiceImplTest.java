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
        UserDto userDto1 = makeUserDto("test1", "test1@test.ru");
        UserDto newUserDto1 = userService.addUser(userDto1);

        UserDto userDto2 = makeUserDto("test2", "test2@test.ru");
        UserDto newUserDto2 = userService.addUser(userDto2);

        UserDto userDtoForUpdate = makeUserDto("test2", "test2@test.ru");
        Assertions.assertThrows(AlreadyExistException.class,
                () -> userService.updateUser(newUserDto1.getId(), userDtoForUpdate));
    }

    @Test
    void updateUserWithWrongId() {
        UserDto userDtoForUpdate = makeUserDto("test2", "test2@test.ru");
        Assertions.assertThrows(NotFoundException.class,
                () -> userService.updateUser(999L, userDtoForUpdate));
    }

    @Test
    void updateUserOnlyName() {
        UserDto userDto1 = makeUserDto("test1", "test1@test.ru");
        UserDto newUserDto1 = userService.addUser(userDto1);

        UserDto userDtoForUpdate = makeUserDto("test2", null);
        UserDto updatedUserDto = userService.updateUser(newUserDto1.getId(), userDtoForUpdate);

        assertThat(newUserDto1.getName(), equalTo("test1"));
        assertThat(updatedUserDto.getName(), equalTo("test2"));
    }

    @Test
    void updateUserOnlyEmail() {
        UserDto userDto1 = makeUserDto("test1", "test1@test.ru");
        UserDto newUserDto1 = userService.addUser(userDto1);

        UserDto userDtoForUpdate = makeUserDto(null, "test2@test.ru");
        UserDto updatedUserDto = userService.updateUser(newUserDto1.getId(), userDtoForUpdate);

        assertThat(newUserDto1.getEmail(), equalTo("test1@test.ru"));
        assertThat(updatedUserDto.getEmail(), equalTo("test2@test.ru"));
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
        UserDto userDto1 = makeUserDto("test1", "test1@test.ru");
        UserDto newUserDto1 = userService.addUser(userDto1);

        UserDto userDto2 = makeUserDto("test2", "test2@test.ru");
        UserDto newUserDto2 = userService.addUser(userDto2);

        Collection<UserDto> userDtos = userService.getAllUsers();
        assertThat(userDtos.size(), equalTo(2));

        assertThat(userDtos, hasItem(allOf(
                hasProperty("id", equalTo(newUserDto1.getId())),
                hasProperty("name", equalTo(newUserDto1.getName())),
                hasProperty("email", equalTo(newUserDto1.getEmail()))
        )));

        assertThat(userDtos, hasItem(allOf(
                hasProperty("id", equalTo(newUserDto2.getId())),
                hasProperty("name", equalTo(newUserDto2.getName())),
                hasProperty("email", equalTo(newUserDto2.getEmail()))
        )));
    }

    @Test
    void getUserByIdWithWrongId() {
        Assertions.assertThrows(NotFoundException.class,
                () -> userService.deleteUser(999L));
    }

    @Test
    void getUserById() {
        UserDto userDto1 = makeUserDto("test1", "test1@test.ru");
        UserDto newUserDto1 = userService.addUser(userDto1);

        UserDto foundUserDto = userService.getUserById(newUserDto1.getId());
        assertThat(foundUserDto, allOf(
                hasProperty("id", equalTo(newUserDto1.getId())),
                hasProperty("name", equalTo(newUserDto1.getName())),
                hasProperty("email", equalTo(newUserDto1.getEmail()))
        ));
    }

    private UserDto makeUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }
}
