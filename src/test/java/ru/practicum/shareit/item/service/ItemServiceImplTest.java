package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private UserDto owner;

    @Test
    public void testGetItemById() {
        UserDto userDto = UserDto.builder()
                .name("User 1")
                .email("user@user.ur")
                .build();

        UserDto owner = userService.addUser(userDto);

        UserDto bookerDto_1 = UserDto.builder()
                .name("Booker 1")
                .email("booker_1@user.ur")
                .build();

        UserDto booker_1 = userService.addUser(bookerDto_1);

        UserDto bookerDto_2 = UserDto.builder()
                .name("Booker 2")
                .email("booker_2@user.ur")
                .build();

        UserDto booker_2 = userService.addUser(bookerDto_2);

        ItemDto itemDto = ItemDto.builder()
                .description("Item 1")
                .name("Item 1")
                .available(true)
                .build();

        ItemDto item = itemService.addItem(owner.getId(), itemDto);

        BookingInputDto bookingInputDto_1 = BookingInputDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().minusMinutes(5))
                .end(LocalDateTime.now().minusMinutes(2))
                .build();

        BookingOutputDto booking_1 = bookingService.addBooking(booker_1.getId(), bookingInputDto_1);

        BookingInputDto bookingInputDto_2 = BookingInputDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusMinutes(2))
                .end(LocalDateTime.now().plusMinutes(5))
                .build();

        BookingOutputDto booking_2 = bookingService.addBooking(booker_2.getId(), bookingInputDto_2);

        // item owner
        ItemWithBookingDto itemWithBookingDto = itemService.getItemById(item.getId(), owner.getId());

        SimpleBookingDto simpleBookingDto_1 = itemWithBookingDto.getLastBooking();
        SimpleBookingDto simpleBookingDto_2 = itemWithBookingDto.getNextBooking();

        assertThat(simpleBookingDto_1)
                .hasFieldOrPropertyWithValue("id", booking_1.getId());

        assertThat(simpleBookingDto_2)
                .hasFieldOrPropertyWithValue("id", booking_2.getId());

        // booker 1 (past)
        itemWithBookingDto = itemService.getItemById(item.getId(), booker_1.getId());

        simpleBookingDto_1 = itemWithBookingDto.getLastBooking();
        simpleBookingDto_2 = itemWithBookingDto.getNextBooking();

        assertThat(simpleBookingDto_1)
                .hasFieldOrPropertyWithValue("id", booking_1.getId());

        assertThat(simpleBookingDto_2)
                .isNull();

        // booker 2 (future)
        itemWithBookingDto = itemService.getItemById(item.getId(), booker_2.getId());

        simpleBookingDto_1 = itemWithBookingDto.getLastBooking();
        simpleBookingDto_2 = itemWithBookingDto.getNextBooking();

        assertThat(simpleBookingDto_1)
                .isNull();

        assertThat(simpleBookingDto_2)
                .hasFieldOrPropertyWithValue("id", booking_2.getId());

    }
}
