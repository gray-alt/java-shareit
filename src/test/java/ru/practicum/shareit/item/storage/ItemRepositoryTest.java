package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {
    private final TestEntityManager em;
    private final ItemRepository itemRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyRepositoryByPersistingAnItem() {
        Item item = new Item();
        item.setName("test");
        item.setDescription("test description");
        item.setAvailable(true);

        Assertions.assertNull(item.getId());
        Item newItem = itemRepository.save(item);
        Assertions.assertNotNull(newItem.getId());

        TypedQuery<Item> query = em.getEntityManager().createQuery(
                "select it from Item it where it.id = :id",
                Item.class);
        Item foundItem = query.setParameter("id", newItem.getId()).getSingleResult();

        Assertions.assertEquals(foundItem.getId(), newItem.getId());
    }

    @Test
    void findAllBySearch() {
        Item item1 = new Item();
        item1.setName("test1");
        item1.setDescription("test 1 description");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setName("test2");
        item2.setDescription("test 2 description");
        item2.setAvailable(true);

        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Item> items = itemRepository.findAllBySearch("another search",
                PageRequest.of(0, 10, Sort.by("id").ascending()));

        assertThat(items.size(), equalTo(0));

        items = itemRepository.findAllBySearch("est",
                PageRequest.of(0, 10, Sort.by("id").ascending()));

        assertThat(items.size(), equalTo(2));
        assertThat(items, hasItem(allOf(
                hasProperty("name", equalTo(item1.getName())),
                hasProperty("description", equalTo(item1.getDescription()))
        )));
        assertThat(items, hasItem(allOf(
                hasProperty("name", equalTo(item2.getName())),
                hasProperty("description", equalTo(item2.getDescription()))
        )));
    }
}
