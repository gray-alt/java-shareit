package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@AutoConfigureTestDatabase
public class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    @MockBean
    private final BookingRepository bookingRepository;

    private final UserDto userDto = makeUserDto("test", "test");
    private final CommentDto commentDto = makeCommentDto("author", "comment", LocalDateTime.now());

    @Test
    void addItemWithWrongOwner() {
        ItemDto itemDto = makeItemDto("test", "test", true, null);
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.addItem(999L, itemDto));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найден пользователь с id"));
    }

    @Test
    void addItem() {
        UserDto newUserDto = userService.addUser(userDto);

        ItemDto itemDto = makeItemDto("test", "test", true, null);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        assertThat(newItemDto, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("name", equalTo(itemDto.getName())),
                hasProperty("description", equalTo(itemDto.getDescription())),
                hasProperty("available", equalTo(itemDto.getAvailable()))
        ));
    }

    @Test
    void updateItemWithWrongOwner() {
        ItemDto itemDto = makeItemDto("test", "test", true, null);
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(999L, 1L, itemDto));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найден пользователь с id"));
    }

    @Test
    void updateItemWithWrongId() {
        UserDto newUserDto = userService.addUser(userDto);

        ItemDto itemDto = makeItemDto("test", "test", true, null);
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(newUserDto.getId(), 999L, itemDto));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найдена вещь с id"));
    }

    @Test
    void updateItemOnlyName() {
        UserDto newUserDto = userService.addUser(userDto);

        ItemDto itemDto = makeItemDto("test", "test", true, null);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        ItemDto itemDtoForUpdate = makeItemDto("test2", null, null, null);
        ItemDto updatedItemDto = itemService.updateItem(newUserDto.getId(), newItemDto.getId(), itemDtoForUpdate);

        assertThat(updatedItemDto, allOf(
                hasProperty("id", equalTo(newItemDto.getId())),
                hasProperty("name", equalTo("test2")),
                hasProperty("description", equalTo("test")),
                hasProperty("available", equalTo(true))
        ));
    }

    @Test
    void updateItemOnlyDescription() {
        UserDto newUserDto = userService.addUser(userDto);

        ItemDto itemDto = makeItemDto("test", "test", true, null);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        ItemDto itemDtoForUpdate = makeItemDto(null, "test2", null, null);
        ItemDto updatedItemDto = itemService.updateItem(newUserDto.getId(), newItemDto.getId(), itemDtoForUpdate);

        assertThat(updatedItemDto, allOf(
                hasProperty("id", equalTo(newItemDto.getId())),
                hasProperty("name", equalTo("test")),
                hasProperty("description", equalTo("test2")),
                hasProperty("available", equalTo(true))
        ));
    }

    @Test
    void updateItemOnlyAvailable() {
        UserDto newUserDto = userService.addUser(userDto);

        ItemDto itemDto = makeItemDto("test", "test", true, null);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        ItemDto itemDtoForUpdate = makeItemDto(null, null, false, null);
        ItemDto updatedItemDto = itemService.updateItem(newUserDto.getId(), newItemDto.getId(), itemDtoForUpdate);

        assertThat(updatedItemDto, allOf(
                hasProperty("id", equalTo(newItemDto.getId())),
                hasProperty("name", equalTo("test")),
                hasProperty("description", equalTo("test")),
                hasProperty("available", equalTo(false))
        ));
    }

    @Test
    void deleteItemWithWrongOwner() {
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.deleteItem(999L, 1L));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найден пользователь с id"));
    }

    @Test
    void deleteItemWithWrongId() {
        UserDto newUserDto = userService.addUser(userDto);

        ItemDto itemDto = makeItemDto("test", "test", true, null);
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.deleteItem(newUserDto.getId(), 999L));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найдена вещь с id"));
    }

    @Test
    void deleteItem() {
        UserDto newUserDto = userService.addUser(userDto);

        ItemDto itemDto = makeItemDto("test", "test", true, null);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        itemService.deleteItem(newUserDto.getId(), newItemDto.getId());

        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.deleteItem(newUserDto.getId(), newItemDto.getId()));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найдена вещь с id"));
    }

    @Test
    void deleteAllOwnerItemsWithWrongOwner() {
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.deleteAllOwnerItems(999L));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найден пользователь с id"));
    }

    @Test
    void deleteAllOwnerItems() {
        UserDto newUserDto = userService.addUser(userDto);

        ItemDto itemDto1 = makeItemDto("test1", "test1", true, null);
        ItemDto newItemDto1 = itemService.addItem(newUserDto.getId(), itemDto1);
        ItemDto itemDto2 = makeItemDto("test2", "test2", true, null);
        ItemDto newItemDto2 = itemService.addItem(newUserDto.getId(), itemDto2);

        Collection<ItemWithBookingDto> itemDtos = itemService.getAllItemsByOwnerId(newUserDto.getId(), 0, 10);

        assertThat(itemDtos.size(), equalTo(2));
        assertThat(itemDtos, hasItem(allOf(
                hasProperty("name", equalTo("test1")),
                hasProperty("description", equalTo("test1"))
        )));
        assertThat(itemDtos, hasItem(allOf(
                hasProperty("name", equalTo("test2")),
                hasProperty("description", equalTo("test2"))
        )));

        itemService.deleteAllOwnerItems(newUserDto.getId());
        itemDtos = itemService.getAllItemsByOwnerId(newUserDto.getId(), 0, 10);
        assertThat(itemDtos.size(), equalTo(0));
    }

    @Test
    void getAllItemsByOwnerIdWithWrongOwner() {
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getAllItemsByOwnerId(999L, 0, 10));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найден пользователь с id"));
    }

    @Test
    void getAllItemsByOwnerId() {
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndLessThan(any(), any(), any(), any()))
                .thenReturn(true);

        UserDto newUserDto = userService.addUser(userDto);

        ItemDto itemDto1 = makeItemDto("test1", "test1", true, null);
        ItemDto newItemDto1 = itemService.addItem(newUserDto.getId(), itemDto1);
        ItemDto itemDto2 = makeItemDto("test2", "test2", true, null);
        ItemDto newItemDto2 = itemService.addItem(newUserDto.getId(), itemDto2);

        CommentDto newCommentDto = itemService.addComment(newUserDto.getId(), newItemDto1.getId(), commentDto);

        Collection<ItemWithBookingDto> itemDtos = itemService.getAllItemsByOwnerId(newUserDto.getId(), 0, 10);

        assertThat(itemDtos.size(), equalTo(2));
        assertThat(itemDtos, hasItem(allOf(
                hasProperty("name", equalTo("test1")),
                hasProperty("description", equalTo("test1"))
        )));
        assertThat(itemDtos, hasItem(allOf(
                hasProperty("name", equalTo("test2")),
                hasProperty("description", equalTo("test2"))
        )));
    }

    @Test
    void getItemsByIdWithWrongUserId() {
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getItemById(1L, 999L));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найден пользователь с id"));
    }

    @Test
    void getItemsByIdWithWrongItemId() {
        UserDto newUserDto = userService.addUser(userDto);

        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getItemById(999L, newUserDto.getId()));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найдена вещь с id"));
    }

    @Test
    void getItemsById() {
        UserDto newUserDto = userService.addUser(userDto);

        ItemDto itemDto = makeItemDto("test", "test", true, null);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        ItemWithBookingDto foundItem = itemService.getItemById(newItemDto.getId(), newUserDto.getId());

        assertThat(foundItem, allOf(
                hasProperty("id", equalTo(newItemDto.getId())),
                hasProperty("name", equalTo("test")),
                hasProperty("description", equalTo("test"))
        ));
    }

    @Test
    void getItemsBySearchWithEmptyTextSearch() {
        UserDto newUserDto = userService.addUser(userDto);

        ItemDto itemDto = makeItemDto("test", "test", true, null);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        Collection<ItemDto> itemDtos = itemService.getItemsBySearch("", 0, 10);
        assertThat(itemDtos.size(), equalTo(0));
    }

    @Test
    void getItemsBySearchWithAnotherTextSearch() {
        UserDto newUserDto = userService.addUser(userDto);

        ItemDto itemDto = makeItemDto("test", "test", true, null);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        Collection<ItemDto> itemDtos = itemService.getItemsBySearch("another text", 0, 10);
        assertThat(itemDtos.size(), equalTo(0));
    }

    @Test
    void getItemsBySearch() {
        UserDto newUserDto = userService.addUser(userDto);

        ItemDto itemDto1 = makeItemDto("test1", "test1", true, null);
        ItemDto newItemDto1 = itemService.addItem(newUserDto.getId(), itemDto1);
        ItemDto itemDto2 = makeItemDto("test2", "test2", true, null);
        ItemDto newItemDto2 = itemService.addItem(newUserDto.getId(), itemDto2);

        Collection<ItemDto> itemDtos = itemService.getItemsBySearch("est", 0, 10);
        assertThat(itemDtos.size(), equalTo(2));
    }

    @Test
    void addCommentWithWrongAuthor() {
        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.addComment(999L, 1L, commentDto));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найден пользователь с id"));
    }

    @Test
    void addCommentWithWrongItem() {
        UserDto newUserDto = userService.addUser(userDto);

        NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.addComment(newUserDto.getId(), 999L, commentDto));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найдена вещь с id"));
    }

    @Test
    void addCommentWithNotApprovedBookings() {
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndLessThan(any(), any(), any(), any()))
                .thenReturn(false);

        UserDto newUserDto = userService.addUser(userDto);

        ItemDto itemDto = makeItemDto("test", "test", true, null);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        NotAllowedException e = Assertions.assertThrows(NotAllowedException.class,
                () -> itemService.addComment(newUserDto.getId(), newItemDto.getId(), commentDto));
        assertThat(e.getMessage(), startsWithIgnoringCase("Не найдена ни одна завершенная аренда"));
    }

    @Test
    void addComment() {
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndLessThan(any(), any(), any(), any()))
                .thenReturn(true);

        UserDto newUserDto = userService.addUser(userDto);

        ItemDto itemDto = makeItemDto("test", "test", true, null);
        ItemDto newItemDto = itemService.addItem(newUserDto.getId(), itemDto);

        CommentDto newCommentDto = itemService.addComment(newUserDto.getId(), newItemDto.getId(), commentDto);

        assertThat(newCommentDto, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("authorName", equalTo(userDto.getName())),
                hasProperty("text", equalTo(commentDto.getText())),
                hasProperty("created", greaterThanOrEqualTo(commentDto.getCreated()))
        ));
    }

    private ItemDto makeItemDto(String name, String description, Boolean isAvailable, Long requestId) {
        return ItemDto.builder()
                .name(name)
                .description(description)
                .available(isAvailable)
                .requestId(requestId)
                .build();
    }

    private UserDto makeUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }

    private CommentDto makeCommentDto(String authorName, String text, LocalDateTime created) {
        return CommentDto.builder()
                .authorName(authorName)
                .text(text)
                .created(created)
                .build();
    }
}
