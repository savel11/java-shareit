package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.exception.InvalidFormatException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto create(NewItemDto newItemDto, Long ownerId) {
        log.info("Пользоваетель с id = " + ownerId + " добавляет новую вещь " + newItemDto);
        User user = checkAndGetUser(ownerId);
        Item item;
        if (newItemDto.getRequestId() == null) {
            item = ItemMapper.toItem(newItemDto, user, null);
        } else {
            Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(newItemDto.getRequestId());
            if (itemRequestOptional.isEmpty()) {
                throw new InvalidFormatException("Запроса по которому добавляется вещь не существует!");
            }
            item = ItemMapper.toItem(newItemDto, user, itemRequestOptional.get());
        }
        log.info("Вещь добавленна в базу!");
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(UpdateItemDto updateItemDto, Long ownerId, Long itemId) {
        log.info("Пользоваетель с id = " + ownerId + " обновляет данные о вещи с id = " + itemId);
        Item item = checkAndGetItem(itemId);
        User owner = checkAndGetUser(ownerId);
        log.trace("Проверка пользователя на владения редактируемой вещи");
        if (!isOwner(owner, item)) {
            log.warn("Данные может редактировать только владелец вещи!");
            throw new NotOwnerException("Невозможно обновить данные о вещи: данные может обновлять только хозяин вещи");
        }
        log.trace("Проверка пройдена!");
        log.info("Данные успешно обновленны!");
        Item newItem = updateFields(item, updateItemDto);
        itemRepository.save(newItem);
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoWithDate getItemById(Long itemId) {
        log.info("Поиск вещи с id = " + itemId);
        Item item = checkAndGetItem(itemId);
        List<Comment> comments = commentRepository.findByItemId(itemId);
        log.info("Вещь найдена");
        return ItemMapper.itemDtoWithDate(item, null, null, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoWithDate> getItems(Long ownerId) {
        log.info("Просмотр всех своих вещей владельцем с id = " + ownerId);
        checkAndGetUser(ownerId);
        log.info("Вещи найдены");
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        Map<Long, BookingShort> lastBookings = bookingRepository.getLastBookingByOwnerId(LocalDateTime.now(), ownerId).stream()
                .collect(Collectors.toMap(BookingShort::getItemId, Function.identity()));
        Map<Long, BookingShort> nextBookings = bookingRepository.getNextBookingByOwnerId(LocalDateTime.now(), ownerId).stream()
                .collect(Collectors.toMap(BookingShort::getItemId, Function.identity()));
        Map<Long, List<Comment>> commentMap = commentRepository.getCommentsByOwnerID(ownerId).stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));
        return items.stream().map(item -> ItemMapper.itemDtoWithDate(item, nextBookings.getOrDefault(item.getId(),
                        null), lastBookings.getOrDefault(item.getId(), null),
                commentMap.getOrDefault(item.getId(), Collections.emptyList()))).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text) {
        log.info("Поиск доступных вещей по запросу: " + text);
        return itemRepository.search(text).stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    @Transactional
    public CommentDto createComment(NewCommentDto newCommentDto, Long itemId, Long userId) {
        log.info("Создание комментария к вещи с id " + itemId + " пользователем с id " + userId);
        User user = checkAndGetUser(userId);
        Item item = checkAndGetItem(itemId);
        Optional<Booking> booking = bookingRepository.findOneByItemIdAndBookerIdAndEndBeforeAndStatus(itemId,
                userId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (booking.isEmpty()) {
            log.warn("Пользоваетль не может оставить комментарий: комментарий можно оставлять после окончания " +
                    "срока использования вещи");

            throw new InvalidFormatException("У пользоваетля нету прав на оставления комментрария!");
        }
        Comment comment = CommentMapper.toComment(newCommentDto, user, item);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private User checkAndGetUser(Long userId) {
        log.trace("Проверка существования пользователя");
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("Пользователь с id = " + userId + " не найден!");
            throw new NotFoundException("Пользователя с id = " + userId + " не существует");
        }
        log.trace("Проверка закончена: Пользователь существует");
        return optionalUser.get();
    }

    private Item checkAndGetItem(Long itemId) {
        log.trace("Проверка существования вещи");
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            log.warn("Вещь с id = " + itemId + " не найдена!");
            throw new NotFoundException("Вещь с id = " + itemId + " не существует");
        }
        log.trace("Проверка закончена: Вещь существует");
        return optionalItem.get();
    }

    private boolean isOwner(User user, Item item) {
        return item.getOwner().equals(user);
    }

    private Item updateFields(Item item, UpdateItemDto updateItemDto) {
        if (updateItemDto.getAvailable() != null) {
            item.setAvailable(updateItemDto.getAvailable());
        }
        if (updateItemDto.getName() != null) {
            item.setName(updateItemDto.getName());
        }
        if (updateItemDto.getDescription() != null) {
            item.setDescription(updateItemDto.getDescription());
        }
        return item;
    }
}
