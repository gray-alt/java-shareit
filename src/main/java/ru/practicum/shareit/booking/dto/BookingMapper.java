package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookingMapper {

    public static Booking mapToBooking(BookingDto bookingDto, Item item, User booker) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.mapToItemDto(booking.getItem()))
                .booker(UserMapper.mapToUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static Collection<BookingDto> mapToBookingDto(Collection<Booking> bookings) {
        return bookings.stream().map(BookingMapper::mapToBookingDto).collect(Collectors.toList());
    }

    public static SimpleBookingDto mapToSimpleBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return SimpleBookingDto.builder()
                .id(booking.getId())
                .bookerId(Optional.ofNullable(booking.getBooker()).map(User::getId).orElse(null))
                .build();
    }
}
