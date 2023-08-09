package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBooking;
import ru.practicum.shareit.item.storage.ItemWithBookingRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemWithBookingRepositoryTest {
    private final TestEntityManager em;
    private final ItemWithBookingRepository itemRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void findItemWithBookingById() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.ru");

        em.persist(user);

        Item item = new Item();
        item.setName("test");
        item.setDescription("test description");
        item.setAvailable(true);
        item.setOwner(user);

        em.persist(item);

        Booking lastBooking = new Booking();
        lastBooking.setItem(item);
        lastBooking.setBooker(user);
        lastBooking.setStatus(BookingStatus.APPROVED);
        lastBooking.setStart(LocalDateTime.now().minusMinutes(10));
        lastBooking.setEnd(LocalDateTime.now().minusMinutes(5));

        em.persist(lastBooking);

        Booking nextBooking = new Booking();
        nextBooking.setItem(item);
        nextBooking.setBooker(user);
        nextBooking.setStatus(BookingStatus.APPROVED);
        nextBooking.setStart(LocalDateTime.now().plusMinutes(5));
        nextBooking.setEnd(LocalDateTime.now().plusMinutes(10));

        em.persist(nextBooking);

        Optional<ItemWithBooking> foundItem = itemRepository.findItemWithBookingById(
                item.getId(), user.getId(), LocalDateTime.now());

        assertThat(foundItem.isEmpty(), equalTo(false));

        ItemWithBooking itemWithBooking = foundItem.get();

        assertThat(itemWithBooking.getName(), equalTo(item.getName()));
        assertThat(itemWithBooking.getLastBooking(), notNullValue());
        assertThat(itemWithBooking.getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(itemWithBooking.getNextBooking(), notNullValue());
        assertThat(itemWithBooking.getNextBooking().getId(), equalTo(nextBooking.getId()));
    }

    @Test
    void findItemWithBookingByOwnerId() {
        // User 1
        User user1 = new User();
        user1.setName("test1");
        user1.setEmail("test1@test.ru");

        em.persist(user1);

        Item item1 = new Item();
        item1.setName("test1");
        item1.setDescription("test 1 description");
        item1.setAvailable(true);
        item1.setOwner(user1);

        em.persist(item1);

        Booking lastBooking1 = new Booking();
        lastBooking1.setItem(item1);
        lastBooking1.setBooker(user1);
        lastBooking1.setStatus(BookingStatus.APPROVED);
        lastBooking1.setStart(LocalDateTime.now().minusMinutes(10));
        lastBooking1.setEnd(LocalDateTime.now().minusMinutes(5));

        em.persist(lastBooking1);

        Booking nextBooking1 = new Booking();
        nextBooking1.setItem(item1);
        nextBooking1.setBooker(user1);
        nextBooking1.setStatus(BookingStatus.APPROVED);
        nextBooking1.setStart(LocalDateTime.now().plusMinutes(5));
        nextBooking1.setEnd(LocalDateTime.now().plusMinutes(10));

        em.persist(nextBooking1);

        // User 2
        User user2 = new User();
        user2.setName("test2");
        user2.setEmail("test2@test.ru");

        em.persist(user2);

        Item item2 = new Item();
        item2.setName("test2");
        item2.setDescription("test 2 description");
        item2.setAvailable(true);
        item2.setOwner(user2);

        em.persist(item2);

        Item item3 = new Item();
        item3.setName("test3");
        item3.setDescription("test 3 description");
        item3.setAvailable(true);
        item3.setOwner(user2);

        em.persist(item3);

        // Test user 1
        List<ItemWithBooking> foundItems = itemRepository.findItemWithBookingByOwnerId(
                user1.getId(), LocalDateTime.now(), PageRequest.of(0, 10, Sort.by("id").ascending()));

        assertThat(foundItems.size(), equalTo(1));

        ItemWithBooking itemWithBooking = foundItems.get(0);

        assertThat(itemWithBooking.getName(), equalTo(item1.getName()));
        assertThat(itemWithBooking.getLastBooking(), notNullValue());
        assertThat(itemWithBooking.getLastBooking().getId(), equalTo(lastBooking1.getId()));
        assertThat(itemWithBooking.getNextBooking(), notNullValue());
        assertThat(itemWithBooking.getNextBooking().getId(), equalTo(nextBooking1.getId()));

        // Test user 2
        foundItems = itemRepository.findItemWithBookingByOwnerId(
                user2.getId(), LocalDateTime.now(), PageRequest.of(0, 10, Sort.by("id").ascending()));

        assertThat(foundItems.size(), equalTo(2));

        itemWithBooking = foundItems.get(0);
        assertThat(itemWithBooking.getName(), equalTo(item2.getName()));
        assertThat(itemWithBooking.getLastBooking(), nullValue());
        assertThat(itemWithBooking.getNextBooking(), nullValue());

        itemWithBooking = foundItems.get(1);
        assertThat(itemWithBooking.getName(), equalTo(item3.getName()));
        assertThat(itemWithBooking.getLastBooking(), nullValue());
        assertThat(itemWithBooking.getNextBooking(), nullValue());
    }
}
