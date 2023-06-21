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
    public Optional<Item> addItem(Long ownerId, Item item) {
        Optional<User> optionalUser = userStorage.getUserById(ownerId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Не найден пользователь с id " + ownerId);
        }
        return itemStorage.addItem(optionalUser.get(), item);
    }

    @Override
    public Optional<Item> updateItem(Long ownerId, Long itemId, Item item) {
        Optional<User> optionalUser = userStorage.getUserById(ownerId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Не найден пользователь с id " + ownerId);
        }

        Optional<Item> optionalItem = itemStorage.updateItem(optionalUser.get(), itemId, item);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Не найдена вещь с id " + itemId +
                    " у владельца с id " + ownerId);
        }
        return optionalItem;
    }

    @Override
    public void deleteItem(Long ownerId, Long itemId) {
        if (userStorage.isUserNotExist(ownerId)) {
            throw new NotFoundException("Не найден пользователь с id " + ownerId);
        } else if (itemStorage.isNotItemExist(ownerId, itemId)) {
            throw new NotFoundException("Не найдена вещь с id " + itemId +
                    " у владельца с id " + ownerId);
        }
        itemStorage.deleteItem(ownerId, itemId);
    }

    @Override
    public void deleteAllOwnerItems(Long ownerId) {
        if (userStorage.isUserNotExist(ownerId)) {
            throw new NotFoundException("Не найден пользователь с id " + ownerId);
        }
        itemStorage.deleteAllOwnerItems(ownerId);
    }

    @Override
    public Collection<Item> getAllItemsByOwnerId(Long ownerId) {
        if (userStorage.isUserNotExist(ownerId)) {
            throw new NotFoundException("Не найден пользователь с id " + ownerId);
        }
        return itemStorage.getAllItemsByOwnerId(ownerId);
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        Optional<Item> optionalItem = itemStorage.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Не найдена вещь с id " + itemId);
        }
        return optionalItem;
    }

    @Override
    public Collection<Item> getItemsBySearch(String textForSearch) {
        if (textForSearch.isEmpty()) {
            return new ArrayList<>();
        }
        return itemStorage.getItemsBySearch(textForSearch);
    }
}
