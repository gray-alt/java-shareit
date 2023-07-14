package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.ItemWithBooking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface ItemWithBookingRepository extends JpaRepository<ItemWithBooking, Long> {
    @Query(value = "select " +
            "   it.id as id, " +
            "   it.name as name, " +
            "   it.description as description, " +
            "   it.is_available as is_available, " +
            "   it.owner_id as owner_id, " +
            "   l_bk.id as last_booking_id, " +
            "   l_bk.booker_id as last_booking_booker_id, " +
            "   n_bk.id as next_booking_id, " +
            "   n_bk.booker_id as next_booking_booker_id, " +
            "   NULL as request_id " +
            "from items as it " +
            "left join (select " +
            "               bk.id, " +
            "               bk.booker_id, " +
            "               bk.item_id " +
            "            from bookings as bk " +
            "            left join items as it " +
            "               on bk.item_id = it.id " +
            "            where " +
            "               bk.item_id = ?1 " +
            "               and it.owner_id = ?2 " +
            "               and bk.status = 'APPROVED' " +
            "               and bk.start_date < ?3 " +
            "            order by bk.end_date Desc " +
            "            limit 1) as l_bk " +
            "   on it.id = l_bk.item_id " +
            "left join (select " +
            "               bk.id, " +
            "               bk.booker_id, " +
            "               bk.item_id " +
            "            from bookings as bk " +
            "            left join items as it " +
            "               on bk.item_id = it.id " +
            "            where " +
            "               bk.item_id = ?1 " +
            "               and it.owner_id = ?2 " +
            "               and bk.status = 'APPROVED' " +
            "               and bk.start_date > ?3 " +
            "            order by bk.start_date Asc " +
            "            limit 1) as n_bk " +
            "   on it.id = n_bk.item_id " +
            "where " +
            "   it.id = ?1 " +
            "limit 1", nativeQuery = true)
    Optional<ItemWithBooking> findItemWithBookingById(Long itemId, Long userId, LocalDateTime date);

    @Query(value = "select " +
            "   it.id as id, " +
            "   it.name as name, " +
            "   it.description as description, " +
            "   it.is_available as is_available, " +
            "   it.owner_id as owner_id, " +
            "   l_bk.id as last_booking_id, " +
            "   l_bk.booker_id as last_booking_booker_id, " +
            "   n_bk.id as next_booking_id, " +
            "   n_bk.booker_id as next_booking_booker_id, " +
            "   NULL as request_id " +
            "from items as it " +
            "left join (select " +
            "               bk.id, " +
            "               bk.booker_id, " +
            "               bk.item_id " +
            "            from bookings as bk " +
            "            where " +
            "               (bk.item_id, bk.start_date) in (select " +
            "                                                       bk.item_id," +
            "                                                       MAX(bk.start_date) " +
            "                                                   from bookings as bk " +
            "                                                       left join items as it " +
            "                                                           on bk.item_id = it.id " +
            "                                                   where " +
            "                                                       it.owner_id = ?1 " +
            "                                                       and bk.status = 'APPROVED' " +
            "                                                       and bk.start_date < ?2 " +
            "                                                   group by " +
            "                                                       bk.item_id) " +
            "            ) as l_bk " +
            "   on it.id = l_bk.item_id " +
            "left join (select " +
            "               bk.id, " +
            "               bk.booker_id, " +
            "               bk.item_id " +
            "            from bookings as bk " +
            "            where " +
            "               (bk.item_id, bk.start_date) in (select " +
            "                                                       bk.item_id," +
            "                                                       MIN(bk.start_date) " +
            "                                                   from bookings as bk " +
            "                                                       left join items as it " +
            "                                                           on bk.item_id = it.id " +
            "                                                   where " +
            "                                                       it.owner_id = ?1 " +
            "                                                       and bk.status = 'APPROVED' " +
            "                                                       and bk.start_date > ?2 " +
            "                                                   group by " +
            "                                                       bk.item_id) " +
            "            ) as n_bk " +
            "   on it.id = n_bk.item_id " +
            "where " +
            "   it.owner_id = ?1 " +
            "order by " +
            "   it.id", nativeQuery = true)
    Collection<ItemWithBooking> findItemWithBookingByOwnerId(Long ownerId, LocalDateTime date);
}
