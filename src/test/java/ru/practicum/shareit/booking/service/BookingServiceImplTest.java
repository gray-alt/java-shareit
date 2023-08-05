package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWithIgnoringCase;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@AutoConfigureTestDatabase
public class BookingServiceImplTest {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    private final LocalDateTime dateMinus10 = LocalDateTime.now().minusMinutes(10);
    private final LocalDateTime dateMinus5 = LocalDateTime.now().minusMinutes(5);
    private final LocalDateTime datePlus10 = LocalDateTime.now().plusMinutes(10);
    private final LocalDateTime datePlus5 = LocalDateTime.now().plusMinutes(5);

    @Test
    void addBookingWithEndBeforeStart() {
        BookingDto bookingDto = makeBookingDto(dateMinus10, dateMinus5, 1L);
        ValidationException e = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.addBooking(1L, bookingDto));
        assertThat(e.getMessage(), startsWithIgnoringCase(
                "Дата окончания бронирования не может быть раньше даты начала бронирования"));
    }

    private BookingDto makeBookingDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        return BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(itemId)
                .build();
    }
}
