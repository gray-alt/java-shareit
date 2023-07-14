package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
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
    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

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
    Collection<Booking> findCurrentBookingsByBookerId(Long bookerId,
                                                      LocalDateTime startDate,
                                                      LocalDateTime endDate);

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
    Collection<Booking> findPastBookingsByBookerId(Long bookerId, LocalDateTime endDate);

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
    Collection<Booking> findFutureBookingsByBookerId(Long bookerId, LocalDateTime startDate);

    // Бронирования по статусу и автору
    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    // Все бронирования по владельцу вещей
    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

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
    Collection<Booking> findCurrentBookingsByItemOwnerId(Long ownerId,
                                                         LocalDateTime startDate,
                                                         LocalDateTime endDate);

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
    Collection<Booking> findPastBookingsByItemOwnerId(Long ownerId, LocalDateTime endDate);

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
    Collection<Booking> findFutureBookingsByItemOwnerId(Long ownerId, LocalDateTime startDate);

    // Бронирования по статусу и владельцу вещей
    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    // Последнее бронирование вещи
    @Query (value = "select " +
            "   bk.id, " +
            "   bk.start_date, " +
            "   bk.end_date, " +
            "   bk.booker_id, " +
            "   bk.item_id, " +
            "   bk.status " +
            "from bookings bk " +
            "left join items it " +
            "   on bk.item_id = it.id " +
            "where " +
            "   bk.item_id = ?1 " +
            "   and it.owner_id = ?2 " +
            "   and bk.status = 'APPROVED' " +
            "   and bk.start_date < ?3 " +
            "order by bk.end_date desc " +
            "limit 1", nativeQuery = true)
    Booking findLastBookingForItem(Long itemId, Long ownerId, LocalDateTime dateTime);

    // Первое будущее бронирование вещи
    @Query (value = "select " +
            "   bk.id, " +
            "   bk.start_date, " +
            "   bk.end_date, " +
            "   bk.booker_id, " +
            "   bk.item_id, " +
            "   bk.status " +
            "from bookings bk " +
            "left join items it " +
            "   on bk.item_id = it.id " +
            "where " +
            "   bk.item_id = ?1 " +
            "   and it.owner_id = ?2 " +
            "   and bk.status = 'APPROVED' " +
            "   and bk.start_date > ?3 " +
            "order by bk.start_date Asc " +
            "limit 1", nativeQuery = true)
    Booking findNextBookingForItem(Long itemId, Long ownerId, LocalDateTime dateTime);

    // Существует завершенное бронирование вещи
    boolean existsByItemIdAndBookerIdAndStatusAndEndLessThan(Long itemId, Long bookerId, BookingStatus status,
                                                             LocalDateTime endDate);
}
