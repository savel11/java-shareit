package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item, User owner);

    Item update(Item item, Long id);

    Optional<Item> getItemById(Long id);

    List<Item> getAllItemByOwner(User user);

    List<Item> search(String text);
}
