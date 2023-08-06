package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentRepository;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBooking;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.storage.ItemWithBookingRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("itemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemWithBookingRepository itemWithBookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + ownerId));

        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null)
        {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElse(null);
        }


        Item item = itemRepository.save(ItemMapper.mapToItem(itemDto, owner, itemRequest));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + ownerId));
        Item foundItem = itemRepository.findByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id " + itemId +
                        " у владельца с id " + ownerId));

        Item item = ItemMapper.mapToItem(itemDto, owner, foundItem.getRequest());

         if (item.getName() != null) {
             foundItem.setName(item.getName());
         }
         if (item.getDescription() != null) {
             foundItem.setDescription(item.getDescription());
         }
         if (item.getAvailable() != null) {
             foundItem.setAvailable(item.getAvailable());
         }

        return ItemMapper.mapToItemDto(itemRepository.save(foundItem));
    }

    @Override
    public void deleteItem(Long ownerId, Long itemId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Не найден пользователь с id " + ownerId);
        } else if (!itemRepository.existsByIdAndOwnerId(itemId, ownerId)) {
            throw new NotFoundException("Не найдена вещь с id " + itemId +
                    " у владельца с id " + ownerId);
        }
        itemRepository.deleteByIdAndOwnerId(itemId, ownerId);
    }

    @Override
    public void deleteAllOwnerItems(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Не найден пользователь с id " + ownerId);
        }
        itemRepository.deleteAllByOwnerId(ownerId);
    }

    @Override
    public Collection<ItemWithBookingDto> getAllItemsByOwnerId(Long ownerId, int from, int size) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Не найден пользователь с id " + ownerId);
        }
        Collection<ItemWithBooking> items = itemWithBookingRepository.findItemWithBookingByOwnerId(ownerId,
                LocalDateTime.now(), getPageRequest(from, size));

        Collection<Comment> allComments = commentRepository.findAllByItemIdIn(
                items.stream().map(ItemWithBooking::getId).collect(Collectors.toList()));

        Collection<ItemWithBookingDto> itemWithBookingDtos = new ArrayList<>();

        for (ItemWithBooking item : items) {
            itemWithBookingDtos.add(ItemMapper.mapToItemWithBookingDto(item,
                    allComments
                            .stream()
                            .filter(comment -> comment.getItemOfCommentId().equals(item.getId()))
                            .collect(Collectors.toList())));
        }

        return itemWithBookingDtos;
    }

    @Override
    public ItemWithBookingDto getItemById(Long itemId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Не найден пользователь с id " + userId);
        }

        LocalDateTime nowDateTime = LocalDateTime.now();

        ItemWithBooking item = itemWithBookingRepository.findItemWithBookingById(itemId, userId, nowDateTime)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id " + itemId));

        return ItemMapper.mapToItemWithBookingDto(item, commentRepository.findAllByItemId(itemId));
    }

    @Override
    public Collection<ItemDto> getItemsBySearch(String textForSearch, int from, int size) {
        if (textForSearch.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<Item> items = itemRepository.findAllBySearch(textForSearch, getPageRequest(from, size));
        return ItemMapper.mapToItemDto(items);
    }

    @Override
    public CommentDto addComment(Long authorId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + authorId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id " + itemId));

        if (!bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndLessThan(itemId, authorId,
                BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new NotAllowedException("Не найдена ни одна завершенная аренда");
        }

        Comment comment = commentRepository.save(CommentMapper.mapToComment(commentDto, author, item,
                LocalDateTime.now()));

        return CommentMapper.mapToCommentDto(comment);
    }

    private PageRequest getPageRequest(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").ascending());
    }
}
