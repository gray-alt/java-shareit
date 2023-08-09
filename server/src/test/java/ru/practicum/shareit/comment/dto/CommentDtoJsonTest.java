package ru.practicum.shareit.comment.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoJsonTest {
    @Autowired
    JacksonTester<CommentDto> json;

    @Test
    void commentDtoTest() throws IOException {
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(5);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("test")
                .authorName("test")
                .created(localDateTime)
                .build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(
                localDateTime.format(formatter));
    }
}