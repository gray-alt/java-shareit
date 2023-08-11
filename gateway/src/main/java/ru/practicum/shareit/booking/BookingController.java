package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
                          @RequestBody @Valid BookingDto bookingDto) {
        log.info("Creating booking {}, userId={}", bookingDto, bookerId);
        return bookingClient.addBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvedBooking(@RequestHeader(name = "X-Sharer-User-Id") Long itemOwnerId,
                               @PathVariable Long bookingId,
                               @RequestParam Boolean approved) {
        log.info("Approved booking bookingId={}, userId={}, approved={}", bookingId, itemOwnerId, approved);
        return bookingClient.approveBooking(itemOwnerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        log.info("Get booking with bookingId={}, userId={}, approved={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllBookingsByBookerId(@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
                                                    @RequestParam(defaultValue = "ALL") BookingState state,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get all bookings with bookerId={}, state={}, from={}, size={}", bookerId, state, from, size);
        return bookingClient.getAllBookingsByBookerId(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingByOwnerId(@RequestHeader(name = "X-Sharer-User-Id") Long itemOwnerId,
                                                  @RequestParam(defaultValue = "ALL") BookingState state,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get all bookings with itemOwnerId={}, state={}, from={}, size={}", itemOwnerId, state, from, size);
        return bookingClient.getAllBookingsByItemOwnerId(itemOwnerId, state, from, size);
    }
}