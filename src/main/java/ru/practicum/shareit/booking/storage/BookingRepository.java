package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Бронирование по id с проверкой автора или владельца вещи
    @Query (value = "select " +
            "   bk.id, " +
            "   bk.start_date, " +
            "   bk.end_date, " +
            "   bk.booker_id, " +
            "   bk.item_id, " +
            "   bk.status " +
            "from bookings bk " +
            "   left join items it " +
            "   on bk.item_id = it.id " +
            "where bk.id = ?1 " +
            "   and (bk.booker_id = ?2 or it.owner_id = ?3) " +
            "limit 1", nativeQuery = true)
    Optional<Booking> findByIdAndBookerIdOrItemOwnerId(Long bookingId, Long bookerId, Long ownerId);

    // Все бронирования по автору
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    // Текущие бронирования по автору
    @Query (value = "select " +
            "   bk.id, " +
            "   bk.start_date, " +
            "   bk.end_date, " +
            "   bk.booker_id, " +
            "   bk.item_id, " +
            "   bk.status " +
            "from bookings bk " +
            "where " +
            "   bk.booker_id = ?1 " +
            "   and bk.start_date <= ?2 " +
            "   and bk.end_date >= ?3 " +
            "order by bk.start_date desc", nativeQuery = true)
    List<Booking> findCurrentBookingsByBookerId(Long bookerId, LocalDateTime startDate, LocalDateTime endDate,
                                                      Pageable pageable);

    // Прошлые бронирования по автору
    @Query (value = "select " +
            "   bk.id, " +
            "   bk.start_date, " +
            "   bk.end_date, " +
            "   bk.booker_id, " +
            "   bk.item_id, " +
            "   bk.status " +
            "from bookings bk " +
            "where " +
            "   bk.booker_id = ?1 " +
            "   and bk.end_date < ?2 " +
            "order by bk.start_date desc", nativeQuery = true)
    List<Booking> findPastBookingsByBookerId(Long bookerId, LocalDateTime endDate, Pageable pageable);

    // Будущие бронирования по автору
    @Query (value = "select " +
            "   bk.id, " +
            "   bk.start_date, " +
            "   bk.end_date, " +
            "   bk.booker_id, " +
            "   bk.item_id, " +
            "   bk.status " +
            "from bookings bk " +
            "where " +
            "   bk.booker_id = ?1 " +
            "   and bk.start_date > ?2 " +
            "order by bk.start_date desc", nativeQuery = true)
    List<Booking> findFutureBookingsByBookerId(Long bookerId, LocalDateTime startDate, Pageable pageable);

    // Бронирования по статусу и автору
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    // Все бронирования по владельцу вещей
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    // Текущие бронирования по владельцу вещей
    @Query (value = "select " +
            "   bk.id, " +
            "   bk.start_date, " +
            "   bk.end_date, " +
            "   bk.booker_id, " +
            "   bk.item_id, " +
            "   bk.status " +
            "from bookings bk " +
            "   left join items it " +
            "   on bk.item_id = it.id " +
            "where " +
            "   it.owner_id = ?1 " +
            "   and bk.start_date <= ?2 " +
            "   and bk.end_date >= ?3 " +
            "order by bk.start_date desc", nativeQuery = true)
    List<Booking> findCurrentBookingsByItemOwnerId(Long ownerId, LocalDateTime startDate, LocalDateTime endDate,
                                                         Pageable pageable);

    // Прошлые бронирования по владельцу вещей
    @Query (value = "select " +
            "   bk.id, " +
            "   bk.start_date, " +
            "   bk.end_date, " +
            "   bk.booker_id, " +
            "   bk.item_id, " +
            "   bk.status " +
            "from bookings bk " +
            "   left join items it " +
            "   on bk.item_id = it.id " +
            "where " +
            "   it.owner_id = ?1 " +
            "   and bk.end_date < ?2 " +
            "order by bk.start_date desc", nativeQuery = true)
    List<Booking> findPastBookingsByItemOwnerId(Long ownerId, LocalDateTime endDate, Pageable pageable);

    // Будущие бронирования по владельцу вещей
    @Query (value = "select " +
            "   bk.id, " +
            "   bk.start_date, " +
            "   bk.end_date, " +
            "   bk.booker_id, " +
            "   bk.item_id, " +
            "   bk.status " +
            "from bookings bk " +
            "   left join items it " +
            "   on bk.item_id = it.id " +
            "where " +
            "   it.owner_id = ?1 " +
            "   and bk.start_date > ?2 " +
            "order by bk.start_date desc", nativeQuery = true)
    List<Booking> findFutureBookingsByItemOwnerId(Long ownerId, LocalDateTime startDate, Pageable pageable);

    // Бронирования по статусу и владельцу вещей
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    // Существует завершенное бронирование вещи
    boolean existsByItemIdAndBookerIdAndStatusAndEndLessThan(Long itemId, Long bookerId, BookingStatus status,
                                                             LocalDateTime endDate);
}
