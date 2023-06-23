package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item addItem(Long ownerId, Item item);

    Item updateItem(Long ownerId, Long itemId, Item item);

    void deleteItem(Long ownerId, Long itemId);

    void deleteAllOwnerItems(Long ownerId);

    Collection<Item> getAllItemsByOwnerId(Long ownerId);

    Item getItemById(Long itemId);

    Collection<Item> getItemsBySearch(String textForSearch);
}
