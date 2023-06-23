package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public class BookingMapper {
    public BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(Optional.ofNullable(booking.getItem()).map(Item::getId).orElse(null))
                .bookerId(Optional.ofNullable(booking.getBooker()).map(User::getId).orElse(null))
                .status(booking.getStatus())
                .build();
    }
}
