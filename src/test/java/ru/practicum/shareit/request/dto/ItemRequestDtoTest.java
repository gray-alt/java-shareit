package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidatingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ItemRequestDtoTest {
    @Autowired
    ValidatingService validatingService;

    @Test
    public void createInvalidItemRequestsTest() {
        Collection<ItemRequestDto> requests = new ArrayList<>();

        //Нет описания
        requests.add(ItemRequestDto.builder()
                .build());

        //Пустое описание
        requests.add(ItemRequestDto.builder()
                .description("")
                .build());

        requests.forEach(x -> assertThrows(ValidationException.class,
                () -> validatingService.validateSimpleRequestDto(x)));
    }
}
