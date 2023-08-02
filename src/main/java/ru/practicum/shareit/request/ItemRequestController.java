package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    ItemRequestDto addItemRequest(@RequestHeader(name = "X-Sharer-User-Id") Long requestorId,
                                  @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addItemRequest(requestorId, itemRequestDto);
    }

    @GetMapping
    Collection<ItemRequestDto> getOwnItemRequests(@RequestHeader(name = "X-Sharer-User-Id") Long requestorId) {
        return itemRequestService.getOwnItemRequests(requestorId);
    }

    @GetMapping("/all")
    Collection<ItemRequestDto> getAllItemRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero  int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getItemRequestById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                      @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
