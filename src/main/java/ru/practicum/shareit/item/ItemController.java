package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(@Qualifier("itemServiceImpl") ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Optional<ItemDto> addItem(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                     @Valid @RequestBody ItemDto itemDto) {
        Optional<Item> optionalItem = itemService.addItem(ownerId, ItemMapper.toItem(itemDto));
        return Optional.of(ItemMapper.toItemDto(optionalItem.orElseThrow()));
    }

    @PatchMapping("/{itemId}")
    public Optional<ItemDto> updateItem(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                        @PathVariable Long itemId,
                                        @RequestBody ItemDto itemDto) {
        Optional<Item> optionalItem = itemService.updateItem(ownerId, itemId, ItemMapper.toItem(itemDto));
        return Optional.of(ItemMapper.toItemDto(optionalItem.orElseThrow()));
    }

    @GetMapping("/{itemId}")
    public Optional<ItemDto> getItemById(@PathVariable Long itemId) {
        Optional<Item> optionalItem = itemService.getItemById(itemId);
        return Optional.of(ItemMapper.toItemDto(optionalItem.orElseThrow()));
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsBuOwnerId(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        Collection<Item> items = itemService.getAllItemsByOwnerId(ownerId);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsBySearch(@RequestParam(name = "text") String textForSearch) {
        Collection<Item> items = itemService.getItemsBySearch(textForSearch);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
