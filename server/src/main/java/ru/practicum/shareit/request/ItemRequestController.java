package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(name = "X-Sharer-User-Id") Long requestorId,
                                  @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addItemRequest(requestorId, itemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getOwnItemRequests(@RequestHeader(name = "X-Sharer-User-Id") Long requestorId) {
        return itemRequestService.getOwnItemRequests(requestorId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllItemRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                  @RequestParam  int from, @RequestParam int size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                      @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
