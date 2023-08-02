package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@RequiredArgsConstructor
@Service("itemRequestServiceImpl")
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDto addItemRequest(Long requestorId, ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + requestorId));
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.mapToItemRequest(
                itemRequestDto, requestor, LocalDateTime.now()));
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public Collection<ItemRequestDto> getOwnItemRequests(Long requestorId) {
        if (!userRepository.existsById(requestorId)) {
            throw new NotFoundException("Не найден пользователь с id " + requestorId);
        }
        Collection<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(
                requestorId);
        return ItemRequestMapper.mapToItemRequestDto(itemRequests);
    }

    @Override
    public Collection<ItemRequestDto> getAllItemRequests(Long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());
        Iterable<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(
                userId, pageRequest);
        return ItemRequestMapper.mapToItemRequestDto(itemRequests);
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long itemRequestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найден пользователь с id " + userId);
        }
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(
                () -> new NotFoundException("Не найден запрос вещи с id " + itemRequestId));
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }
}
