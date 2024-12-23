package ru.practicum.shareit.item.service;

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

public class ItemService {
    @Autowired
    @Qualifier("InMemoryItemRepository")
    private ItemRepository itemRepository;

    @Autowired
    @Qualifier("InMemoryUserRepository")
    private UserRepository userRepository;


    public ItemDto create(NewItemDto newItemDto, Long ownerId) {
        User user = checkAndGetUser(ownerId);
        Item item = ItemMapper.toItem(newItemDto);
        return ItemMapper.toItemDto(itemRepository.create(item, user));
    }

    public ItemDto update(UpdateItemDto updateItemDto, Long ownerId, Long itemId) {
        Item item = checkAndGetItem(itemId);
        User owner = checkAndGetUser(ownerId);
        if (!isOwner(owner, item)) {
            throw new NotOwnerException("Невозможно обновить данные о вещи: данные может обновлять только хозяин вещи");
        }
        return ItemMapper.toItemDto(itemRepository.update(updateFields(item, updateItemDto), itemId));
    }

    public ItemDto getItemById(Long itemId) {
        Item item = checkAndGetItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    public List<OwnerItemDto> getItems(Long ownerId) {
        User owner = checkAndGetUser(ownerId);
        return itemRepository.getAllItemByOwner(owner).stream().map(ItemMapper::toOwnerItemDto).toList();
    }

    public List<ItemDto> searchItems(String text) {
        return itemRepository.search(text).stream().map(ItemMapper::toItemDto).toList();
    }

    private User checkAndGetUser(Long userId) {
        Optional<User> optionalUser = userRepository.getById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователя с id = " + userId + " не существует");
        }
        return optionalUser.get();
    }

    private Item checkAndGetItem(Long itemId) {
        Optional<Item> optionalItem = itemRepository.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Вещь с id = " + itemId + " не существует");
        }
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
