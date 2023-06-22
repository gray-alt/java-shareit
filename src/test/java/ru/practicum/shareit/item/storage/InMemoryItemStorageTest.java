package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InMemoryItemStorageTest {
    private final InMemoryItemStorage itemStorage;
    private final InMemoryUserStorage userStorage;
    private User owner;

    @BeforeEach
    public void beforeEach() {
        if (userStorage.isNotExist(1L)) {
            User newUser = User.builder()
                    .name("New user")
                    .email("User@email.ru")
                    .build();

            Optional<User> optionalUser = userStorage.addUser(newUser);
            owner = optionalUser.orElseThrow();
        } else {
            Optional<User> optionalUser = userStorage.getUserById(1L);
            owner = optionalUser.orElseThrow();
        }
    }

    @Test
    public void testAddItem() {
        Item newItem = Item.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();

        Optional<Item> optionalItem = itemStorage.addItem(owner, newItem);

        assertThat(optionalItem)
                .isPresent();
    }

    @Test
    public void testUpdateItemName() {
        Item newItem = Item.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();

        Optional<Item> optionalItem = itemStorage.addItem(owner, newItem);

        assertThat(optionalItem)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item)
                        .hasFieldOrPropertyWithValue("name", "Item name")
                        .hasFieldOrPropertyWithValue("description", "Item description")
                        .hasFieldOrPropertyWithValue("available", true)
        );

        Item itemForUpdate = Item.builder()
                .name("Updated name")
                .build();

        optionalItem = itemStorage.updateItem(owner, optionalItem.get().getId(), itemForUpdate);

        assertThat(optionalItem)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item)
                        .hasFieldOrPropertyWithValue("name", "Updated name")
                        .hasFieldOrPropertyWithValue("description", "Item description")
                        .hasFieldOrPropertyWithValue("available", true)
                );
    }

    @Test
    public void testUpdateItemDescription() {
        Item newItem = Item.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();

        Optional<Item> optionalItem = itemStorage.addItem(owner, newItem);

        assertThat(optionalItem)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item)
                        .hasFieldOrPropertyWithValue("name", "Item name")
                        .hasFieldOrPropertyWithValue("description", "Item description")
                        .hasFieldOrPropertyWithValue("available", true)
                );

        Item itemForUpdate = Item.builder()
                .description("Updated description")
                .build();

        optionalItem = itemStorage.updateItem(owner, optionalItem.get().getId(), itemForUpdate);

        assertThat(optionalItem)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item)
                        .hasFieldOrPropertyWithValue("name", "Item name")
                        .hasFieldOrPropertyWithValue("description", "Updated description")
                        .hasFieldOrPropertyWithValue("available", true)
                );
    }

    @Test
    public void testUpdateItemAvailable() {
        Item newItem = Item.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();

        Optional<Item> optionalItem = itemStorage.addItem(owner, newItem);

        assertThat(optionalItem)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item)
                        .hasFieldOrPropertyWithValue("name", "Item name")
                        .hasFieldOrPropertyWithValue("description", "Item description")
                        .hasFieldOrPropertyWithValue("available", true)
                );

        Item itemForUpdate = Item.builder()
                .available(false)
                .build();

        optionalItem = itemStorage.updateItem(owner, optionalItem.get().getId(), itemForUpdate);

        assertThat(optionalItem)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item)
                        .hasFieldOrPropertyWithValue("name", "Item name")
                        .hasFieldOrPropertyWithValue("description", "Item description")
                        .hasFieldOrPropertyWithValue("available", false)
                );
    }

    @Test
    public void testUpdateItemWithWrongId() {
        Item itemForUpdate = Item.builder()
                .available(false)
                .build();

        Optional<Item> optionalItem = itemStorage.updateItem(owner, 999L, itemForUpdate);

        assertThat(optionalItem)
                .isEmpty();
    }

    @Test
    public void testUpdateItemWithWrongOwner() {
        Item newItem = Item.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();

        Optional<Item> optionalItem = itemStorage.addItem(owner, newItem);

        assertThat(optionalItem)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item)
                        .hasFieldOrPropertyWithValue("name", "Item name")
                        .hasFieldOrPropertyWithValue("description", "Item description")
                        .hasFieldOrPropertyWithValue("available", true)
                );

        User newUser = User.builder()
                .name("New owner")
                .email("new_owner@email.ru")
                .build();

        Optional<User> optionalUser = userStorage.addUser(newUser);
        User newOwner = optionalUser.orElseThrow();

        Item itemForUpdate = Item.builder()
                .available(false)
                .build();

        optionalItem = itemStorage.updateItem(newOwner, optionalItem.get().getId(), itemForUpdate);

        assertThat(optionalItem)
                .isEmpty();
    }

    @Test
    public void testDeleteItem() {
        Item newItem = Item.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();

        Optional<Item> optionalItem = itemStorage.addItem(owner, newItem);

        assertThat(optionalItem)
                .isPresent();

        itemStorage.deleteItem(owner.getId(), optionalItem.get().getId());

        optionalItem = itemStorage.getItemById(optionalItem.get().getId());

        assertThat(optionalItem)
                .isEmpty();
    }

    @Test
    public void testDeleteItemWithWrongId() {
        Item newItem = Item.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();

        Optional<Item> optionalItem = itemStorage.addItem(owner, newItem);

        assertThat(optionalItem)
                .isPresent();

        itemStorage.deleteItem(owner.getId(), 999L);

        optionalItem = itemStorage.getItemById(optionalItem.get().getId());

        assertThat(optionalItem)
                .isPresent();
    }

    @Test
    public void testDeleteItemWithWrongOwner() {
        Item newItem = Item.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();

        Optional<Item> optionalItem = itemStorage.addItem(owner, newItem);

        assertThat(optionalItem)
                .isPresent();

        itemStorage.deleteItem(999L, optionalItem.get().getId());

        optionalItem = itemStorage.getItemById(optionalItem.get().getId());

        assertThat(optionalItem)
                .isPresent();
    }

    @Test
    public void testDeleteAllOwnerItems() {
        Item newItem = Item.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();

        Optional<Item> optionalItem = itemStorage.addItem(owner, newItem);

        assertThat(optionalItem)
                .isPresent();

        Collection<Item> items = itemStorage.getAllItemsByOwnerId(owner.getId());

        assertThat(items)
                .isNotEmpty();

        itemStorage.deleteAllOwnerItems(owner.getId());

        items = itemStorage.getAllItemsByOwnerId(owner.getId());

        assertThat(items)
                .isEmpty();
    }

    @Test
    public void testDeleteAllOwnerItemsWithWrongOwnerId() {
        Item newItem = Item.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();

        Optional<Item> optionalItem = itemStorage.addItem(owner, newItem);

        assertThat(optionalItem)
                .isPresent();

        Collection<Item> items = itemStorage.getAllItemsByOwnerId(owner.getId());

        assertThat(items)
                .isNotEmpty();

        itemStorage.deleteAllOwnerItems(999L);

        Collection<Item> itemsAfterDelete = itemStorage.getAllItemsByOwnerId(owner.getId());

        assertThat(itemsAfterDelete)
                .isNotEmpty()
                .size().isEqualTo(items.size());
    }

    @Test
    public void testGetAllItemsByOwnerId() {
        Collection<Item> items = itemStorage.getAllItemsByOwnerId(owner.getId());

        Item newItem = Item.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();

        Optional<Item> optionalItem = itemStorage.addItem(owner, newItem);

        assertThat(optionalItem)
                .isPresent();

        Collection<Item> itemsAfterAddItem = itemStorage.getAllItemsByOwnerId(owner.getId());

        assertThat(itemsAfterAddItem)
                .isNotEmpty()
                .size().isEqualTo(items.size() + 1);
    }

    @Test
    public void testGetAllItemsByOwnerIdWithWrongOwnerId() {
        Collection<Item> items = itemStorage.getAllItemsByOwnerId(999L);

        assertThat(items)
                .isEmpty();
    }

    @Test
    public void testGetItemById() {
        Item newItem = Item.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .build();

        Optional<Item> optionalItem = itemStorage.addItem(owner, newItem);

        assertThat(optionalItem)
                .isPresent();

        Optional<Item> optionalFoundItem = itemStorage.getItemById(optionalItem.get().getId());

        assertThat(optionalFoundItem)
                .isPresent()
                .hasValueSatisfying(item -> assertThat(item)
                        .hasFieldOrPropertyWithValue("id", optionalItem.get().getId())
                );
    }

    @Test
    public void testGetItemByIdWithWrongId() {
        Optional<Item> optionalItem = itemStorage.getItemById(999L);

        assertThat(optionalItem)
                .isEmpty();
    }

    @Test
    public void testGetItemsBySearchForName() {
        Item newItem = Item.builder()
                .name("Search name")
                .description("Item description")
                .available(true)
                .build();

        Optional<Item> optionalItem = itemStorage.addItem(owner, newItem);

        assertThat(optionalItem)
                .isPresent();

        Collection<Item> items = itemStorage.getItemsBySearch("Search");

        assertThat(items)
                .isNotEmpty()
                .size().isEqualTo(1);
    }

    @Test
    public void testGetItemsBySearchForDescription() {
        Item newItem = Item.builder()
                .name("Item name")
                .description("Find me description")
                .available(true)
                .build();

        Optional<Item> optionalItem = itemStorage.addItem(owner, newItem);

        assertThat(optionalItem)
                .isPresent();

        Collection<Item> items = itemStorage.getItemsBySearch("find me");

        assertThat(items)
                .isNotEmpty()
                .size().isEqualTo(1);
    }

    @Test
    public void testGetItemsBySearchForEmptyText() {
        Collection<Item> items = itemStorage.getItemsBySearch("");

        assertThat(items)
                .isEmpty();
    }
}
