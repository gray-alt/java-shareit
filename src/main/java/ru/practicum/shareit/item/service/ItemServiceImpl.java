package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Component("itemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemServiceImpl(@Qualifier("inMemoryItemStorage") ItemStorage itemStorage,
                           @Qualifier("inMemoryUserStorage") UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Item addItem(Long ownerId, Item item) {
        User user = userStorage.getUserById(ownerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + ownerId));

        return itemStorage.addItem(user, item).orElseThrow();
    }

    @Override
    public Item updateItem(Long ownerId, Long itemId, Item item) {
        User user = userStorage.getUserById(ownerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + ownerId));

        Optional<Item> optionalItem = itemStorage.updateItem(user, itemId, item);
        return optionalItem.orElseThrow(() -> new NotFoundException("Не найдена вещь с id " + itemId +
                " у владельца с id " + ownerId));
    }

    @Override
    public void deleteItem(Long ownerId, Long itemId) {
        if (userStorage.isNotExist(ownerId)) {
            throw new NotFoundException("Не найден пользователь с id " + ownerId);
        } else if (itemStorage.isNotExist(ownerId, itemId)) {
            throw new NotFoundException("Не найдена вещь с id " + itemId +
                    " у владельца с id " + ownerId);
        }
        itemStorage.deleteItem(ownerId, itemId);
    }

    @Override
    public void deleteAllOwnerItems(Long ownerId) {
        if (userStorage.isNotExist(ownerId)) {
            throw new NotFoundException("Не найден пользователь с id " + ownerId);
        }
        itemStorage.deleteAllOwnerItems(ownerId);
    }

    @Override
    public Collection<Item> getAllItemsByOwnerId(Long ownerId) {
        if (userStorage.isNotExist(ownerId)) {
            throw new NotFoundException("Не найден пользователь с id " + ownerId);
        }
        return itemStorage.getAllItemsByOwnerId(ownerId);
    }

    @Override
    public Item getItemById(Long itemId) {
        Optional<Item> optionalItem = itemStorage.getItemById(itemId);
        return optionalItem.orElseThrow(() -> new NotFoundException("Не найдена вещь с id " + itemId));
    }

    @Override
    public Collection<Item> getItemsBySearch(String textForSearch) {
        if (textForSearch.isEmpty()) {
            return new ArrayList<>();
        }
        return itemStorage.getItemsBySearch(textForSearch);
    }
}
