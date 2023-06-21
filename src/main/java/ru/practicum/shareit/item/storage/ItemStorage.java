package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Optional<Item> addItem(User owner, Item item);

    Optional<Item> updateItem(User owner, Long itemId, Item item);

    void deleteItem(Long ownerId, Long itemId);

    void deleteAllOwnerItems(Long ownerId);

    Collection<Item> getAllItemsByOwnerId(Long ownerId);

    Optional<Item> getItemById(Long itemId);

    Collection<Item> getItemsBySearch(String textForSearch);

    boolean isItemExist(Long ownerId, Long itemId);

    boolean isNotItemExist(Long ownerId, Long itemId);
}
