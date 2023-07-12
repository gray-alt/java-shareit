package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(@Qualifier("bookingServiceImpl") BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    BookingOutputDto addBooking(@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
                                    @Valid @RequestBody BookingInputDto bookingInputDto) {
        return bookingService.addBooking(bookerId, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")
    BookingOutputDto approvedBooking(@RequestHeader(name = "X-Sharer-User-Id") Long itemOwnerId,
                                    @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        return bookingService.approveBooking(itemOwnerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    BookingOutputDto getBookingById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping()
    Collection<BookingOutputDto> getAllBookingsByBookerId(@RequestHeader(name = "X-Sharer-User-Id") Long bookerId,
                                                         @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getAllBookingsByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    Collection<BookingOutputDto> getAllBookingByOwnerId(@RequestHeader(name = "X-Sharer-User-Id") Long itemOwnerId,
                                                       @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getAllBookingsByItemOwnerId(itemOwnerId, state);
    }
}
