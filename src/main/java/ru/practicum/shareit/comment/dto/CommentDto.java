package ru.practicum.shareit.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentDto {
    Long id;
    @NotNull
    @NotBlank(message = "Отзыв не может быть пустым")
    String text;
    String authorName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.000'Z'")
    LocalDateTime created;
}
