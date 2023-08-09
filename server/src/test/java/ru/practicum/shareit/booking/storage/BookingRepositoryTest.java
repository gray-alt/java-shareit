package ru.practicum.shareit.booking.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {
    private final TestEntityManager em;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private User newOwner;
    private User newBooker;
    private Item newItem;
    private Booking newPastBooking;
    private Booking newCurrentBooking;
    private Booking newFutureBooking;

    private final PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").ascending());

    @BeforeEach
    void beforeEach() {
        User owner = new User();
        owner.setEmail("test@test.ru");
        owner.setName("test");

        Assertions.assertNull(owner.getId());
        newOwner = userRepository.save(owner);
        Assertions.assertNotNull(newOwner.getId());

        User booker = new User();
        booker.setEmail("booker@test.ru");
        booker.setName("booker");

        Assertions.assertNull(booker.getId());
        newBooker = userRepository.save(booker);
        Assertions.assertNotNull(newBooker.getId());

        Item item = new Item();
        item.setOwner(newOwner);
        item.setName("test");
        item.setDescription("test description");
        item.setAvailable(true);

        Assertions.assertNull(item.getId());
        newItem = itemRepository.save(item);
        Assertions.assertNotNull(newItem.getId());

        Booking pastBooking = new Booking();
        pastBooking.setItem(newItem);
        pastBooking.setBooker(newBooker);
        pastBooking.setStart(LocalDateTime.now().minusMinutes(10));
        pastBooking.setEnd(LocalDateTime.now().minusMinutes(5));
        pastBooking.setStatus(BookingStatus.WAITING);

        Assertions.assertNull(pastBooking.getId());
        newPastBooking = bookingRepository.save(pastBooking);
        Assertions.assertNotNull(newPastBooking.getId());

        Booking currentBooking = new Booking();
        currentBooking.setItem(newItem);
        currentBooking.setBooker(newBooker);
        currentBooking.setStart(LocalDateTime.now().minusMinutes(10));
        currentBooking.setEnd(LocalDateTime.now().plusMinutes(10));
        currentBooking.setStatus(BookingStatus.WAITING);

        Assertions.assertNull(currentBooking.getId());
        newCurrentBooking = bookingRepository.save(currentBooking);
        Assertions.assertNotNull(newCurrentBooking.getId());

        Booking futureBooking = new Booking();
        futureBooking.setItem(newItem);
        futureBooking.setBooker(newBooker);
        futureBooking.setStart(LocalDateTime.now().plusMinutes(5));
        futureBooking.setEnd(LocalDateTime.now().plusMinutes(10));
        futureBooking.setStatus(BookingStatus.WAITING);

        Assertions.assertNull(futureBooking.getId());
        newFutureBooking = bookingRepository.save(futureBooking);
        Assertions.assertNotNull(newFutureBooking.getId());
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyRepositoryByPersistingAnBooking() {
        TypedQuery<Booking> query = em.getEntityManager().createQuery(
                "select book from Booking book where book.id = :id",
                Booking.class);
        Booking foundBooking = query.setParameter("id", newFutureBooking.getId()).getSingleResult();

        assertThat(foundBooking, allOf(
                hasProperty("id", equalTo(newFutureBooking.getId())),
                hasProperty("start", equalTo(newFutureBooking.getStart())),
                hasProperty("end", equalTo(newFutureBooking.getEnd())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue())
        ));

        assertThat(foundBooking.getItemIdOfBooking(), equalTo(newItem.getId()));
        assertThat(foundBooking.getBooker().getId(), equalTo(newBooker.getId()));
    }

    @Test
    void findByIdAndBookerIdOrItemOwnerId() {
        Optional<Booking> foundBooking = bookingRepository.findByIdAndBookerIdOrItemOwnerId(
                newFutureBooking.getId(), newBooker.getId(), 999L);

        assertThat(foundBooking.isEmpty(), equalTo(false));
        assertThat(foundBooking.get(), allOf(
                hasProperty("id", equalTo(newFutureBooking.getId())),
                hasProperty("start", equalTo(newFutureBooking.getStart())),
                hasProperty("end", equalTo(newFutureBooking.getEnd())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue())
        ));

        foundBooking = bookingRepository.findByIdAndBookerIdOrItemOwnerId(
                newFutureBooking.getId(), 999L, newOwner.getId());

        assertThat(foundBooking.isEmpty(), equalTo(false));
        assertThat(foundBooking.get(), allOf(
                hasProperty("id", equalTo(newFutureBooking.getId())),
                hasProperty("start", equalTo(newFutureBooking.getStart())),
                hasProperty("end", equalTo(newFutureBooking.getEnd())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue())
        ));

        foundBooking = bookingRepository.findByIdAndBookerIdOrItemOwnerId(
                newFutureBooking.getId(), 999L, 999L);

        assertThat(foundBooking.isEmpty(), equalTo(true));
    }

    @Test
    void findCurrentBookingsByBookerId() {
        Collection<Booking> foundBookings = bookingRepository.findCurrentBookingsByBookerId(
                newBooker.getId(), LocalDateTime.now(), LocalDateTime.now(), pageRequest);

        assertThat(foundBookings.size(), equalTo(1));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newCurrentBooking.getId())),
                hasProperty("start", equalTo(newCurrentBooking.getStart())),
                hasProperty("end", equalTo(newCurrentBooking.getEnd())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue())
        )));
    }

    @Test
    void findPastBookingsByBookerId() {
        Collection<Booking> foundBookings = bookingRepository.findPastBookingsByBookerId(
                newBooker.getId(), LocalDateTime.now(), pageRequest);

        assertThat(foundBookings.size(), equalTo(1));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newPastBooking.getId())),
                hasProperty("start", equalTo(newPastBooking.getStart())),
                hasProperty("end", equalTo(newPastBooking.getEnd())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue())
        )));
    }

    @Test
    void findFutureBookingsByBookerId() {
        Collection<Booking> foundBookings = bookingRepository.findFutureBookingsByBookerId(
                newBooker.getId(), LocalDateTime.now(), pageRequest);

        assertThat(foundBookings.size(), equalTo(1));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newFutureBooking.getId())),
                hasProperty("start", equalTo(newFutureBooking.getStart())),
                hasProperty("end", equalTo(newFutureBooking.getEnd())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue())
        )));
    }

    @Test
    void findCurrentBookingsByItemOwnerId() {
        Collection<Booking> foundBookings = bookingRepository.findCurrentBookingsByItemOwnerId(
                newOwner.getId(), LocalDateTime.now(), LocalDateTime.now(), pageRequest);

        assertThat(foundBookings.size(), equalTo(1));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newCurrentBooking.getId())),
                hasProperty("start", equalTo(newCurrentBooking.getStart())),
                hasProperty("end", equalTo(newCurrentBooking.getEnd())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue())
        )));
    }

    @Test
    void findPastBookingsByItemOwnerId() {
        Collection<Booking> foundBookings = bookingRepository.findPastBookingsByItemOwnerId(
                newOwner.getId(), LocalDateTime.now(), pageRequest);

        assertThat(foundBookings.size(), equalTo(1));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newPastBooking.getId())),
                hasProperty("start", equalTo(newPastBooking.getStart())),
                hasProperty("end", equalTo(newPastBooking.getEnd())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue())
        )));
    }

    @Test
    void findFutureBookingsByItemOwnerId() {
        Collection<Booking> foundBookings = bookingRepository.findFutureBookingsByItemOwnerId(
                newOwner.getId(), LocalDateTime.now(), pageRequest);

        assertThat(foundBookings.size(), equalTo(1));
        assertThat(foundBookings, hasItem(allOf(
                hasProperty("id", equalTo(newFutureBooking.getId())),
                hasProperty("start", equalTo(newFutureBooking.getStart())),
                hasProperty("end", equalTo(newFutureBooking.getEnd())),
                hasProperty("item", notNullValue()),
                hasProperty("booker", notNullValue())
        )));
    }
}
