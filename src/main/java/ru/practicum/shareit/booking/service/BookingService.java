package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {
    BookingOutputDto addBooking(Long bookerId, BookingInputDto bookingInputDto);

    BookingOutputDto approveBooking(Long itemOwnerId, Long bookingId, Boolean approved);

    BookingOutputDto getBookingById(Long userId, Long bookingId);

    Collection<BookingOutputDto> getAllBookingsByBookerId(Long bookerId, BookingState state);

    Collection<BookingOutputDto> getAllBookingsByItemOwnerId(Long itemOwnerId, BookingState state);
}
