package ru.practicum.shareit.exception;


import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Service
public class ValidatingService {
    private final Validator validator;

    public ValidatingService(Validator validator) {
        this.validator = validator;
    }

    public void validateSimpleUserDto(UserDto user) throws ValidationException {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации");
        }
    }

    public void validateSimpleItemDto(ItemDto item) throws ValidationException {
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(item);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации");
        }
    }
}
