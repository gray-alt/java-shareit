package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    BookingDto addBooking(@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
                                    @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    BookingDto approvedBooking(@RequestHeader(name = "X-Sharer-User-Id") Long itemOwnerId,
                                    @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        return bookingService.approveBooking(itemOwnerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    BookingDto getBookingById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping()
    Collection<BookingDto> getAllBookingsByBookerId(@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
                                                    @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getAllBookingsByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    Collection<BookingDto> getAllBookingByOwnerId(@RequestHeader(name = "X-Sharer-User-Id") Long itemOwnerId,
                                                  @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getAllBookingsByItemOwnerId(itemOwnerId, state);
    }
}
