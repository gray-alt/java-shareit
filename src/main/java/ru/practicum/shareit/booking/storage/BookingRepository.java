package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Все бронирования по автору
    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    // Текущие бронирования по автору
    Collection<Booking> findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long bookerId,
                                                                                             LocalDateTime startDate,
                                                                                             LocalDateTime endDate);

    // Прошлые бронирования по автору
    Collection<Booking> findAllByBookerIdAndEndLessThanOrderByStartDesc(Long bookerId, LocalDateTime endDate);

    // Будущие бронирования по автору
    Collection<Booking> findAllByBookerIdAndStartGreaterThanOrderByStartDesc(Long bookerId, LocalDateTime startDate);

    // Бронирования по статусу и автору
    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    // Все бронирования по владельцу вещей
    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    // Текущие бронирования по владельцу вещей
    Collection<Booking> findAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Long ownerId,
                                                                                             LocalDateTime startDate,
                                                                                             LocalDateTime endDate);

    // Прошлые бронирования по владельцу вещей
    Collection<Booking> findAllByItemOwnerIdAndEndLessThanOrderByStartDesc(Long ownerId, LocalDateTime endDate);

    // Будущие бронирования по владельцу вещей
    Collection<Booking> findAllByItemOwnerIdAndStartGreaterThanOrderByStartDesc(Long ownerId, LocalDateTime startDate);

    // Бронирования по статусу и владельцу вещей
    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    // Последнее бронирование вещи
    Booking findTop1ByItemIdAndItemOwnerIdAndStatusAndStartLessThanOrderByEndDesc(Long itemId, Long ownerId,
                                                                                BookingStatus status,
                                                                                LocalDateTime dateTime);

    // Первое будущее бронирование вещи
    Booking findTop1ByItemIdAndItemOwnerIdAndStatusAndStartGreaterThanOrderByStartAsc(Long itemId, Long ownerId,
                                                                                      BookingStatus status,
                                                                                      LocalDateTime dateTime);

    // Существует завершенное бронирование вещи
    boolean existsByItemIdAndBookerIdAndStatusAndEndLessThan(Long itemId, Long bookerId, BookingStatus status,
                                                             LocalDateTime endDate);
}
