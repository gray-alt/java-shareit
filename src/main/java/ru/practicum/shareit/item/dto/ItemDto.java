package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemDto {
    Long id;
    @NotNull
    @NotBlank(message = "Название не может быть пустым")
    String name;
    @NotNull
    @NotBlank(message = "Описание не может быть пустым")
    String description;
    @NotNull(message = "Не указана доступность для аренды")
    Boolean available;
    Long ownerId;
    Long requestId;
}
