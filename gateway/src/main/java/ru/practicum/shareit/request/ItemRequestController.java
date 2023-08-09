package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(name = "X-Sharer-User-Id") Long requestorId,
                                                 @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Creating request {}", itemRequestDto);
        return itemRequestClient.addItemRequest(requestorId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnItemRequests(@RequestHeader(name = "X-Sharer-User-Id") Long requestorId) {
        log.info("Get own request with requestorId={}", requestorId);
        return itemRequestClient.getOwnItemRequests(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get all request with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                      @PathVariable Long requestId) {
        log.info("Get request with userId={}, requestId={}", userId, requestId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
