package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ItemService {
    @Autowired
    @Qualifier("InMemoryItemRepository")
    private ItemRepository itemRepository;

    @Autowired
    @Qualifier("InMemoryUserRepository")
    private UserRepository userRepository;


    public ItemDto create(NewItemDto newItemDto, Long ownerId) {
        log.info("Пользоваетель с id = " + ownerId + " добавляет новую вещь " + newItemDto);
        User user = checkAndGetUser(ownerId);
        Item item = ItemMapper.toItem(newItemDto);
        log.info("Вещь добавленна в базу!");
        return ItemMapper.toItemDto(itemRepository.create(item, user));
    }

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
        return ItemMapper.toItemDto(itemRepository.update(updateFields(item, updateItemDto), itemId));
    }

    public ItemDto getItemById(Long itemId) {
        log.info("Поиск вещи с id = " + itemId);
        Item item = checkAndGetItem(itemId);
        log.info("Вещь найдена");
        return ItemMapper.toItemDto(item);
    }

    public List<OwnerItemDto> getItems(Long ownerId) {
        log.info("Просмотр всех своих вещей владельцем с id = " + ownerId);
        User owner = checkAndGetUser(ownerId);
        log.info("Вещи найдены");
        return itemRepository.getAllItemByOwner(owner).stream().map(ItemMapper::toOwnerItemDto).toList();
    }

    public List<ItemDto> searchItems(String text) {
        log.info("Поиск доступных вещей по запросу: " + text);
        return itemRepository.search(text).stream().map(ItemMapper::toItemDto).toList();
    }

    private User checkAndGetUser(Long userId) {
        log.trace("Проверка существования пользователя");
        Optional<User> optionalUser = userRepository.getById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("Пользователь с id = " + userId + " не найден!");
            throw new NotFoundException("Пользователя с id = " + userId + " не существует");
        }
        log.trace("Проверка закончена: Пользователь существует");
        return optionalUser.get();
    }

    private Item checkAndGetItem(Long itemId) {
        log.trace("Проверка существования вещи");
        Optional<Item> optionalItem = itemRepository.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            log.warn("Вещь с id = " + itemId + " не найдена!");
            throw new NotFoundException("Вещь с id = " + itemId + " не существует");
        }
        log.trace("Проверка закончена: Вещь существует");
        return optionalItem.get();
    }

    private boolean isOwner(User user, Item item) {
        if (item.getOwner().equals(user)) {
            return true;
        }
        return false;
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
