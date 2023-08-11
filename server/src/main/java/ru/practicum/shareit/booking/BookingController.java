package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
                                    @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@RequestHeader(name = "X-Sharer-User-Id") Long itemOwnerId,
                                    @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        return bookingService.approveBooking(itemOwnerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping()
    public Collection<BookingDto> getAllBookingsByBookerId(@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
                                                    @RequestParam BookingState state,
                                                    @RequestParam int from,
                                                    @RequestParam int size) {
        return bookingService.getAllBookingsByBookerId(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingByOwnerId(@RequestHeader(name = "X-Sharer-User-Id") Long itemOwnerId,
                                                  @RequestParam BookingState state,
                                                  @RequestParam int from,
                                                  @RequestParam int size) {
        return bookingService.getAllBookingsByItemOwnerId(itemOwnerId, state, from, size);
    }
}
