package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
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
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(Optional.ofNullable(itemRequest.getItems()).orElse(new HashSet<>())
                        .stream().map(ItemMapper::mapToItemDto).collect(Collectors.toSet()))
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
