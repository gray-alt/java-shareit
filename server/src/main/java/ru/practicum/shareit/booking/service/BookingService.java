package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {
    BookingDto addBooking(Long bookerId, BookingDto bookingDto);

    BookingDto approveBooking(Long itemOwnerId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    Collection<BookingDto> getAllBookingsByBookerId(Long bookerId, BookingState state, int from, int size);

    Collection<BookingDto> getAllBookingsByItemOwnerId(Long itemOwnerId, BookingState state, int from, int size);
}
