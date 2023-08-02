package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

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
        //return itemService.getItemById(itemId, userId);
        return itemService.getItemByIdAlternativeQuery(itemId, userId);
    }

    @GetMapping
    public Collection<ItemWithBookingDto> getAllItemsBuOwnerId(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                                               @RequestParam(defaultValue = "0")
                                                               @PositiveOrZero int from,
                                                               @RequestParam(defaultValue = "10") @Positive int size) {
        return itemService.getAllItemsByOwnerIdAlternativeQuery(ownerId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsBySearch(@RequestParam(name = "text") String textForSearch,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return itemService.getItemsBySearch(textForSearch, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(name = "X-Sharer-User-Id") Long authorId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(authorId, itemId, commentDto);
    }
}
