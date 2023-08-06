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
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    private final BookingService bookingService;

    private final UserDto bookerDto = makeUserDto(1L, "booker", "booker@test.ru");
    private final ItemDto itemDto = makeItemDto(1L, "item", "item", true, null);
    private final BookingDto bookingDto = makeBookingDto(1L, LocalDateTime.now().plusMinutes(5),
            LocalDateTime.now().plusMinutes(10), itemDto, bookerDto, BookingStatus.WAITING);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    void addBookingInThePast() throws Exception {
        when(bookingService.addBooking(any(), any()))
                .thenReturn(bookingDto);

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

    @Test
    void addBooking() throws Exception {
        when(bookingService.addBooking(any(), any()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    void approvedBooking() throws Exception {
        BookingDto approvingBookingDto = makeBookingDto(1L, LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(10), itemDto, bookerDto, BookingStatus.APPROVED);

        when(bookingService.approveBooking(any(), any(), any()))
                .thenReturn(approvingBookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(any(), any()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    void getAllBookingsByBookerId() throws Exception {
        when(bookingService.getAllBookingsByBookerId(any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    void getAllBookingByOwnerId() throws Exception {
        when(bookingService.getAllBookingsByItemOwnerId(any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().format(formatter))))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().format(formatter))))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.WAITING.toString())));
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
