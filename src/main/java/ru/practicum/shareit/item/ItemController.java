package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(@Qualifier("itemServiceImpl") ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                     @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                        @PathVariable Long itemId,
                                        @RequestBody ItemDto itemDto) {
        return itemService.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto getItemById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemWithBookingDto> getAllItemsBuOwnerId(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        return itemService.getAllItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsBySearch(@RequestParam(name = "text") String textForSearch) {
        return itemService.getItemsBySearch(textForSearch);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(name = "X-Sharer-User-Id") Long authorId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(authorId, itemId, commentDto);
    }
}
