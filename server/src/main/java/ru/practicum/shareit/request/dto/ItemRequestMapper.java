package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto, User requestor, LocalDateTime created) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(requestor)
                .created(created)
                .build();
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        Set<ItemDto> items = new HashSet<>();
        if (itemRequest.getItems() != null) {
            items = itemRequest.getItems().stream().map(ItemMapper::mapToItemDto).collect(Collectors.toSet());
        }
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }

    public static Collection<ItemRequestDto> mapToItemRequestDto(Collection<ItemRequest> itemRequests) {
        return itemRequests.stream().map(ItemRequestMapper::mapToItemRequestDto).collect(Collectors.toList());
    }

    public static Collection<ItemRequestDto> mapToItemRequestDto(Iterable<ItemRequest> itemRequests) {
        Collection<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestDtos.add(mapToItemRequestDto(itemRequest));
        }
        return itemRequestDtos;
    }
}
