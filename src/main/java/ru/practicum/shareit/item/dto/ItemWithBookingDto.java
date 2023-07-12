package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.SimpleBookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.Collection;

@RequiredArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemWithBookingDto {
    Long id;
    String name;
    String description;
    Boolean available;
    SimpleBookingDto lastBooking;
    SimpleBookingDto nextBooking;
    Collection<CommentDto> comments;
}