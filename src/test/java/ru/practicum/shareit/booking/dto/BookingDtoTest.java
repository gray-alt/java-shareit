package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidatingService;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BookingDtoTest {
    @Autowired
    ValidatingService validatingService;

    @Test
    public void createInvalidBookingTest() {
        Collection<BookingDto> bookings = new ArrayList<>();

        //Отсутствие даты начала аренды
        bookings.add(BookingDto.builder()
                .end(LocalDateTime.now().plusMinutes(5))
                .itemId(1L)
                .build());

        //Начало аренды в прошлом
        bookings.add(BookingDto.builder()
                .start(LocalDateTime.now().minusMinutes(5))
                .end(LocalDateTime.now().plusMinutes(5))
                .itemId(1L)
                .build());

        //Отсутствие даты окончания аренды
        bookings.add(BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(5))
                .itemId(1L)
                .build());

        //Окончание аренды в прошлом
        bookings.add(BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(5))
                .end(LocalDateTime.now().minusMinutes(5))
                .itemId(1L)
                .build());

        //Отсутствие id вещи
        bookings.add(BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(5))
                .end(LocalDateTime.now().plusMinutes(10))
                .build());

        bookings.forEach(x -> assertThrows(ValidationException.class,
                () -> validatingService.validateSimpleBookingDto(x)));
    }
}
