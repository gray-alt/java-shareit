package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Component("inMemoryItemStorage")
@RequiredArgsConstructor
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    private final HashMap<Long, HashMap<Long, Item>> itemsByOwner;
    private final HashMap<Long, Item> items;
    private long lastId;

    @Override
    public Optional<Item> addItem(User owner, Item item) {
        Item newItem = Item.builder()
                .id(++lastId)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(owner)
                .request(item.getRequest())
                .build();

        if (!itemsByOwner.containsKey(owner.getId())) {
            itemsByOwner.put(owner.getId(), new HashMap<>());
        }
        itemsByOwner.get(owner.getId()).put(newItem.getId(), newItem);
        items.put(newItem.getId(), newItem);
        log.info("Добавлена вещь " + newItem.getName() + " с id " + newItem.getId() + " у пользователя с id " +
                owner.getId());
        return Optional.of(newItem);
    }

    @Override
    public Optional<Item> updateItem(User owner, Long itemId, Item item) {
        if (!itemsByOwner.containsKey(owner.getId())) {
            return Optional.empty();
        }

        Item foundItem = itemsByOwner.get(owner.getId()).get(itemId);
        if (foundItem == null) {
            return Optional.empty();
        }

        Item newItem = Item.builder()
                .id(itemId)
                .name(item.getName() != null ? item.getName() : foundItem.getName())
                .description(item.getDescription() != null ? item.getDescription() : foundItem.getDescription())
                .available(item.getAvailable() != null ? item.getAvailable() : foundItem.getAvailable())
                .owner(owner)
                .request(foundItem.getRequest())
                .build();

        itemsByOwner.get(owner.getId()).put(newItem.getId(), newItem);
        items.put(newItem.getId(), newItem);
        log.info("Обновлена вещь " + newItem.getName() + " с id " + newItem.getId() + " у пользователя с id " +
                owner.getId());
        return Optional.of(newItem);
    }

    @Override
    public void deleteItem(Long ownerId, Long itemId) {
        if (itemsByOwner.containsKey(ownerId)) {
            itemsByOwner.get(ownerId).remove(itemId);
            items.remove(itemId);
            log.info("Удалена вещь с id " + itemId + " у пользователя с id " + ownerId);
        }
    }

    @Override
    public void deleteAllOwnerItems(Long ownerId) {
        if (itemsByOwner.containsKey(ownerId)) {
            for (HashMap<Long, Item> itemsMap : itemsByOwner.values()) {
                for (Item item : itemsMap.values()) {
                    items.remove(item.getId());
                }
            }
            itemsByOwner.remove(ownerId);
        }
    }

    @Override
    public Collection<Item> getAllItemsByOwnerId(Long ownerId) {
        if (itemsByOwner.containsKey(ownerId)) {
            return new ArrayList<>(itemsByOwner.get(ownerId).values());
        }
        return new ArrayList<>();
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        if (items.containsKey(itemId)) {
            return Optional.of(items.get(itemId));
        }
        return Optional.empty();
    }

    @Override
    public Collection<Item> getItemsBySearch(String textForSearch) {
        if (textForSearch.isBlank()) {
            return new ArrayList<>();
        }
        Collection<Item> foundItems = new ArrayList<>();
        String itemName;
        String itemDescription;
        textForSearch = textForSearch.toLowerCase();
        for (Item item : items.values()) {
            itemName = item.getName().toLowerCase();
            itemDescription = item.getDescription().toLowerCase();
            if (item.getAvailable()
                    && (itemName.contains(textForSearch) || itemDescription.contains(textForSearch))) {
                foundItems.add(item);
            }
        }
        return foundItems;
    }

    @Override
    public boolean isItemExist(Long ownerId, Long itemId) {
        if (itemsByOwner.containsKey(ownerId)) {
            return itemsByOwner.get(ownerId).containsKey(itemId);
        }
        return false;
    }

    @Override
    public boolean isNotItemExist(Long ownerId, Long itemId) {
        return !isItemExist(ownerId, itemId);
    }
}
