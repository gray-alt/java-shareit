package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(Optional.ofNullable(itemRequest.getRequestor()).map(User::getId).orElse(null))
                .created(itemRequest.getCreated())
                .build();
    }
}
