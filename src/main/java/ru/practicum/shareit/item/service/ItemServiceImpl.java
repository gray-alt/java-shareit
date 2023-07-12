package ru.practicum.shareit.item.service;

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
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Service("itemServiceImpl")
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + ownerId));
        Item item = itemRepository.save(ItemMapper.mapToItem(itemDto, owner));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + ownerId));
        Item foundItem = itemRepository.findByIdAndOwnerId(itemId, ownerId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id " + itemId +
                        " у владельца с id " + ownerId));
        Item item = itemRepository.save(foundItem.withItem(ItemMapper.mapToItem(itemDto, owner)));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public void deleteItem(Long ownerId, Long itemId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Не найден пользователь с id " + ownerId);
        } else if (itemRepository.existsByIdAndOwnerId(itemId, ownerId)) {
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
    public Collection<ItemWithBookingDto> getAllItemsByOwnerId(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Не найден пользователь с id " + ownerId);
        }
        Collection<Item> items = itemRepository.findAllByOwnerIdOrderById(ownerId);
        Collection<ItemWithBookingDto> itemWithBookingDtos = new ArrayList<>();

        LocalDateTime nowDateTime = LocalDateTime.now();

        for (Item item : items) {
            itemWithBookingDtos.add(
                    ItemMapper.mapToItemWithBookingDto(
                            item,
                            bookingRepository.findTop1ByItemIdAndItemOwnerIdAndStatusAndStartLessThanOrderByEndDesc(
                                    item.getId(), ownerId, BookingStatus.APPROVED, nowDateTime),
                            bookingRepository.findTop1ByItemIdAndItemOwnerIdAndStatusAndStartGreaterThanOrderByStartAsc(
                                    item.getId(), ownerId, BookingStatus.APPROVED, nowDateTime),
                            commentRepository.findAllByItemId(item.getId())
                    )
            );
        }
        return itemWithBookingDtos;
    }

    @Override
    public ItemWithBookingDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id " + itemId));

        if (!userRepository.existsById(userId)) {
                throw new NotFoundException("Не найден пользователь с id " + userId);
        }

        LocalDateTime nowDateTime = LocalDateTime.now();

        return ItemMapper.mapToItemWithBookingDto(
                item,
                bookingRepository.findTop1ByItemIdAndItemOwnerIdAndStatusAndStartLessThanOrderByEndDesc(itemId, userId,
                        BookingStatus.APPROVED, nowDateTime),
                bookingRepository.findTop1ByItemIdAndItemOwnerIdAndStatusAndStartGreaterThanOrderByStartAsc(itemId,
                        userId, BookingStatus.APPROVED, nowDateTime),
                commentRepository.findAllByItemId(itemId)
        );
    }

    @Override
    public Collection<ItemDto> getItemsBySearch(String textForSearch) {
        if (textForSearch.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<Item> items = itemRepository.findAllBySearch(textForSearch);
        return ItemMapper.mapToItemDto(items);
    }

    @Override
    public CommentDto addComment(Long authorId, Long itemId, CommentDto commentDto) {
        if (!bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndLessThan(itemId, authorId,
                BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new NotAllowedException("Не найдена ни одна завершенная аренда");
        }
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + authorId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id " + itemId));

        Comment comment = commentRepository.save(CommentMapper.mapToComment(commentDto, author, item,
                LocalDateTime.now()));

        return CommentMapper.mapToCommentDto(comment);
    }
}
