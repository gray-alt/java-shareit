package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(Long ownerId, ItemDto itemDto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto item);

    void deleteItem(Long ownerId, Long itemId);

    void deleteAllOwnerItems(Long ownerId);

    Collection<ItemWithBookingDto> getAllItemsByOwnerId(Long ownerId);

    ItemWithBookingDto getItemById(Long itemId, Long userId);

    Collection<ItemDto> getItemsBySearch(String textForSearch);

    CommentDto addComment(Long authorId, Long itemId, CommentDto commentDto);

    ItemWithBookingDto getItemByIdAlternativeQuery(Long itemId, Long userId);

    Collection<ItemWithBookingDto> getAllItemsByOwnerIdAlternativeQuery(Long ownerId);
}
