package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(Long requestorId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDto> getOwnItemRequests(Long requestorId);

    Collection<ItemRequestDto> getAllItemRequests(Long userId, int from, int size);

    ItemRequestDto getItemRequestById(Long userId, Long itemRequestId);
}
