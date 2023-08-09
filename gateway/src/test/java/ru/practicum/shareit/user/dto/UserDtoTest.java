package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidatingService;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserDtoTest {
    @Autowired
    ValidatingService validatingService;

    @Test
    public void createInvalidUsersTest() {
        Collection<UserDto> users = new ArrayList<>();

        //Нет адреса почты
        users.add(UserDto.builder()
                .name("Name")
                .build());

        //Неправильный адреса почты
        users.add(UserDto.builder()
                .email("11111")
                .name("Name")
                .build());

        //Нет имени
        users.add(UserDto.builder()
                .email("1@1.ru")
                .build());

        //Пустое имя
        users.add(UserDto.builder()
                .email("1@1.ru")
                .name("")
                .build());

        users.forEach(x -> assertThrows(ValidationException.class, () -> validatingService.validateSimpleUserDto(x)));
    }
}
