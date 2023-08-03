package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
@Service("bookingServiceImpl")
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(Long bookerId, BookingDto bookingDto) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (end.isBefore(start)) {
            throw new ValidationException("Дата окончания бронирования не может быть раньше даты начала бронирования");
        } else if (end.isEqual(start)) {
            throw new ValidationException("Дата окончания бронирования не может быть равной дате начала бронирования");
        }

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + bookerId));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id " + bookingDto.getItemId()));

        if (!item.getAvailable()) {
            throw new NotAllowedException("Вещь с id " + item.getId() + " недоступна для бронирования");
        }

        if (item.getOwnerOfItemId().equals(bookerId)) {
            throw new NotFoundException("Нельзя забронировать собственную вещь");
        }

        Booking booking = bookingRepository.save(BookingMapper.mapToBooking(bookingDto, item, booker));
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto approveBooking(Long itemOwnerId, Long bookingId, Boolean approved) {
        if (!userRepository.existsById(itemOwnerId)) {
            throw new NotFoundException("Не найден пользователь с id " + itemOwnerId);
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id " + bookingId));

        if (!booking.getOwnerId().equals(itemOwnerId)) {
            throw new NotFoundException("Пользователь с id " + itemOwnerId +
                    " не является владельцем вещи с id " + booking.getItemId());
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new NotAllowedException("Нельзя изменить статус подтвержденного бронирования");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найден пользователь с id " + userId);
        }
        Booking booking = bookingRepository.findByIdAndBookerIdOrItemOwnerId(bookingId, userId, userId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id " + bookingId));
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getAllBookingsByBookerId(Long bookerId, BookingState state, int from, int size) {
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException("Не найден пользователь с id " + bookerId);
        }

        Collection<Booking> bookings;
        int page = from > 0 ? from / size : 0;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("start").descending());
        PageRequest nativePageRequest = PageRequest.of(page, size, Sort.by("start_date").descending());
        LocalDateTime currentDate = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByBookerId(bookerId, currentDate, currentDate,
                        nativePageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByBookerId(bookerId, currentDate, nativePageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByBookerId(bookerId, currentDate, nativePageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId,
                        BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId,
                        BookingStatus.REJECTED, pageRequest);
                break;
            default:
                bookings = new ArrayList<>();
        }
        return BookingMapper.mapToBookingDto(bookings);
    }

    @Override
    public Collection<BookingDto> getAllBookingsByItemOwnerId(Long itemOwnerId, BookingState state, int from, int size) {
        if (!userRepository.existsById(itemOwnerId)) {
            throw new NotFoundException("Не найден пользователь с id " + itemOwnerId);
        }

        Collection<Booking> bookings;
        int page = from > 0 ? from / size : 0;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("start").descending());
        PageRequest nativePageRequest = PageRequest.of(page, size, Sort.by("start_date").descending());
        LocalDateTime currentDate = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(itemOwnerId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByItemOwnerId(itemOwnerId, currentDate, currentDate,
                        nativePageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findPastBookingsByItemOwnerId(itemOwnerId, currentDate, nativePageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureBookingsByItemOwnerId(itemOwnerId, currentDate,
                        nativePageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(itemOwnerId,
                        BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(itemOwnerId,
                        BookingStatus.REJECTED, pageRequest);
                break;
            default:
                bookings = new ArrayList<>();
        }
        return BookingMapper.mapToBookingDto(bookings);
    }
}
