package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingDto {
    Long id;
    @NotNull
    @FutureOrPresent
    LocalDateTime start;
    @NotNull
    @Future
    LocalDateTime end;
    @NotNull
    Long itemId;
    ItemDto item;
    UserDto booker;
    BookingStatus status;
}
