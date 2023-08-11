package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    private final BookingClient bookingClient;

    private final UserDto bookerDto = makeUserDto(1L, "booker", "booker@test.ru");
    private final ItemDto itemDto = makeItemDto(1L, "item", "item", true, null);

    @Test
    void addBookingInThePast() throws Exception {
        when(bookingClient.addBooking(any(), any()))
                .thenReturn(null);

        BookingDto pastBookingDto = makeBookingDto(1L, LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().plusMinutes(10), itemDto, bookerDto, BookingStatus.WAITING);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(pastBookingDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    private UserDto makeUserDto(Long id, String name, String email) {
        return UserDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    private ItemDto makeItemDto(Long id, String name, String description, Boolean isAvailable, Long requestId) {
        return ItemDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(isAvailable)
                .requestId(requestId)
                .build();
    }

    private BookingDto makeBookingDto(Long id, LocalDateTime start, LocalDateTime end, ItemDto item, UserDto booker,
                                      BookingStatus status) {
        return BookingDto.builder()
                .id(id)
                .start(start)
                .end(end)
                .itemId(item.getId())
                .item(item)
                .booker(booker)
                .status(status)
                .build();
    }
}
