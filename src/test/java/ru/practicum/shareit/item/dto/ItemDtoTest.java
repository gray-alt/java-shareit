package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidatingService;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ItemDtoTest {
    @Autowired
    ValidatingService validatingService;

    @Test
    public void createInvalidItemsTest() {
        Collection<ItemDto> items = new ArrayList<>();

        //Нет названия
        items.add(ItemDto.builder()
                .description("Description")
                .available(true)
                .build());

        //Пустое название
        items.add(ItemDto.builder()
                .name("")
                .description("Description")
                .available(true)
                .build());

        //Не описания
        items.add(ItemDto.builder()
                .name("Name")
                .available(true)
                .build());

        //Пустое описание
        items.add(ItemDto.builder()
                .name("Name")
                .description("")
                .available(true)
                .build());

        //Нет доступности
        items.add(ItemDto.builder()
                .name("Name")
                .description("Description")
                .build());

        items.forEach(x -> assertThrows(ValidationException.class, () -> validatingService.validateSimpleItemDto(x)));
    }
}
