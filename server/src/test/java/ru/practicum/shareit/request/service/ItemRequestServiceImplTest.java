package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@AutoConfigureTestDatabase
public class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    private final UserDto userDto = makeUserDto("test", "test");
    private final ItemRequestDto itemRequestDto = makeItemRequestDto("test");

    private UserDto newUserDto;
    private ItemRequestDto newItemRequestDto;

    @BeforeEach
    void beforeEach() {
        newUserDto = userService.addUser(userDto);
        newItemRequestDto = itemRequestService.addItemRequest(newUserDto.getId(), itemRequestDto);
    }

    @Test
    void addItemRequestWithWrongRequestor() {
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.addItemRequest(999L, itemRequestDto));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найден пользователь с id"));
    }

    @Test
    void addItemRequest() {
        assertThat(newItemRequestDto, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(itemRequestDto.getDescription())),
                hasProperty("created", notNullValue())
        ));
    }

    @Test
    void getOwnItemRequestsWithWrongOwner() {
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getOwnItemRequests(999L));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найден пользователь с id"));
    }

    @Test
    void getOwnItemRequests() {
        Collection<ItemRequestDto> itemRequestDtos = itemRequestService.getOwnItemRequests(newUserDto.getId());
        assertThat(itemRequestDtos.size(), equalTo(1));
        assertThat(itemRequestDtos, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(itemRequestDto.getDescription())),
                hasProperty("created", notNullValue())
        )));
    }

    @Test
    void getAllItemRequestsByRequestorId() {
        Collection<ItemRequestDto> itemRequestDtos = itemRequestService.getAllItemRequests(newUserDto.getId(), 0, 10);
        assertThat(itemRequestDtos.size(), equalTo(0));
    }

    @Test
    void getAllItemRequests() {
        Collection<ItemRequestDto> itemRequestDtos = itemRequestService.getAllItemRequests(999L, 0, 10);
        assertThat(itemRequestDtos.size(), equalTo(1));
        assertThat(itemRequestDtos, hasItem(allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(itemRequestDto.getDescription())),
                hasProperty("created", notNullValue())
        )));
    }

    @Test
    void getItemRequestByIdWithWrongUserId() {
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(999L, newItemRequestDto.getId()));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найден пользователь с id"));
    }

    @Test
    void getItemRequestByIdWithWrongRequestId() {
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(newUserDto.getId(), 999L));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найден запрос вещи с id"));
    }

    @Test
    void getItemRequestById() {
        ItemRequestDto itemRequestDtos = itemRequestService.getItemRequestById(newUserDto.getId(), newItemRequestDto.getId());
        assertThat(itemRequestDtos, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("description", equalTo(itemRequestDto.getDescription())),
                hasProperty("created", notNullValue())
        ));
    }

    private ItemRequestDto makeItemRequestDto(String description) {
        return ItemRequestDto.builder()
                .description(description)
                .build();
    }

    private UserDto makeUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }
}
