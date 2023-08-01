package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    public void testGetItemById() {
        UserDto userDto = UserDto.builder()
                .name("User 1")
                .email("user@user.ur")
                .build();

        UserDto owner = userService.addUser(userDto);

        UserDto bookerDto1 = UserDto.builder()
                .name("Booker 1")
                .email("booker_1@user.ur")
                .build();

        UserDto booker1 = userService.addUser(bookerDto1);

        UserDto bookerDto2 = UserDto.builder()
                .name("Booker 2")
                .email("booker_2@user.ur")
                .build();

        UserDto booker2 = userService.addUser(bookerDto2);

        ItemDto itemDto = ItemDto.builder()
                .description("Item 1")
                .name("Item 1")
                .available(true)
                .build();

        ItemDto item = itemService.addItem(owner.getId(), itemDto);

        BookingDto bookingDto1 = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().minusMinutes(5))
                .end(LocalDateTime.now().minusMinutes(2))
                .build();

        BookingDto booking1 = bookingService.addBooking(booker1.getId(), bookingDto1);
        bookingService.approveBooking(owner.getId(), booking1.getId(), true);

        BookingDto bookingDto2 = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(2))
                .end(LocalDateTime.now().plusMinutes(5))
                .build();

        BookingDto booking2 = bookingService.addBooking(booker2.getId(), bookingDto2);
        bookingService.approveBooking(owner.getId(), booking2.getId(), true);

        // item owner
        ItemWithBookingDto itemWithBookingDto = itemService.getItemById(item.getId(), owner.getId());

        SimpleBookingDto simpleBookingDto1 = itemWithBookingDto.getLastBooking();
        SimpleBookingDto simpleBookingDto2 = itemWithBookingDto.getNextBooking();

        assertThat(simpleBookingDto1)
                .hasFieldOrPropertyWithValue("id", booking1.getId());

        assertThat(simpleBookingDto2)
                .hasFieldOrPropertyWithValue("id", booking2.getId());

        // booker 1 (past)
        itemWithBookingDto = itemService.getItemById(item.getId(), booker1.getId());

        simpleBookingDto1 = itemWithBookingDto.getLastBooking();
        simpleBookingDto2 = itemWithBookingDto.getNextBooking();

        assertThat(simpleBookingDto1)
                .isNull();

        assertThat(simpleBookingDto2)
                .isNull();

        // booker 2 (future)
        itemWithBookingDto = itemService.getItemById(item.getId(), booker2.getId());

        simpleBookingDto1 = itemWithBookingDto.getLastBooking();
        simpleBookingDto2 = itemWithBookingDto.getNextBooking();

        assertThat(simpleBookingDto1)
                .isNull();

        assertThat(simpleBookingDto2)
                .isNull();

        // new search item
        ItemWithBookingDto newItem = itemService.getItemByIdAlternativeQuery(item.getId(), booker1.getId());

        assertThat(newItem).hasFieldOrPropertyWithValue("lastBooking", null);
        assertThat(newItem).hasFieldOrPropertyWithValue("nextBooking", null);

        newItem = itemService.getItemByIdAlternativeQuery(item.getId(), owner.getId());

        assertThat(newItem.getLastBooking()).hasFieldOrPropertyWithValue("id", booking1.getId());
        assertThat(newItem.getNextBooking()).hasFieldOrPropertyWithValue("id", booking2.getId());

        // new search all items
        Collection<ItemWithBookingDto> newItems = itemService.getAllItemsByOwnerIdAlternativeQuery(owner.getId(), 0, 10);
        assertThat(newItems).size().isEqualTo(1);
    }
}
