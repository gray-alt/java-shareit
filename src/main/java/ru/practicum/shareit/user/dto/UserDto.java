package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDto {
    Long id;
    @NotNull(message = "Имя должно быть заполнено")
    @NotBlank(message = "Имя не может быть пустым")
    String name;
    @NotNull(message = "Адрес электронной почты должен быть заполнен")
    @Email(message = "Неверно указан адрес электронной почты")
    String email;
}
