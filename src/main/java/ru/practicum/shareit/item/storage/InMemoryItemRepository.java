package ru.practicum.shareit.item.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
@Qualifier("InMemoryItemRepository")
public class InMemoryItemRepository implements ItemRepository {
    @Autowired
    @Qualifier("InMemoryUserRepository")
    private UserRepository userRepository;
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    private Long generatedId() {
        return ++id;
    }

    @Override
    public Item create(Item item, User owner) {
        item.setId(generatedId());
        item.setOwner(owner);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item, Long id) {
        Item oldItem = items.get(id);
        oldItem.setName(item.getName());
        oldItem.setDescription(item.getDescription());
        oldItem.setAvailable(item.getAvailable());
        items.put(id, oldItem);
        return oldItem;
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        if (items.containsKey(id)) {
            return Optional.of(items.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<Item> getAllItemByOwner(User user) {
        return items.values().stream().filter(item -> item.getOwner().equals(user)).toList();
    }

    @Override
    public List<Item> search(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return items.values().stream().filter(Item::getAvailable)
                .filter(item -> item.getName().toUpperCase().contains(text) ||
                        item.getDescription().toUpperCase().contains(text)).toList();
    }
}
