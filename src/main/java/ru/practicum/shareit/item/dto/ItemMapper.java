package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBooking;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemMapper {
    public static Item mapToItem(ItemDto itemDto, User owner, ItemRequest itemRequest) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .request(itemRequest)
                .build();
    }

    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(Optional.ofNullable(item.getRequest()).map(ItemRequest::getId).orElse(null))
                .build();
    }

    public static Collection<ItemDto> mapToItemDto(Collection<Item> items) {
        return items.stream().map(ItemMapper::mapToItemDto).collect(Collectors.toList());
    }

    public static ItemWithBookingDto mapToItemWithBookingDto(Item item, Booking lastBooking, Booking nextBooking,
                                                             Collection<Comment> comments) {
        return ItemWithBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(BookingMapper.mapToSimpleBookingDto(lastBooking))
                .nextBooking(BookingMapper.mapToSimpleBookingDto(nextBooking))
                .comments(CommentMapper.mapToCommentDto(comments))
                .build();
    }

    public static ItemWithBookingDto mapToItemWithBookingAlternativeQueryDto(ItemWithBooking item,
                                                                             Collection<Comment> comments) {
        return ItemWithBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(BookingMapper.mapToSimpleBookingDto(item.getLastBooking()))
                .nextBooking(BookingMapper.mapToSimpleBookingDto(item.getNextBooking()))
                .comments(CommentMapper.mapToCommentDto(comments))
                .build();
    }
}
