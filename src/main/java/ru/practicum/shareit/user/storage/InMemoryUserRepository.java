package ru.practicum.shareit.user.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Qualifier("InMemoryUserRepository")
public class InMemoryUserRepository implements UserRepository {
    Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    private Long generatedId() {
        return ++id;
    }

    @Override
    public User create(User user) {
        user.setId(generatedId());
        users.put(user.getId(), User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build());
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), User.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build());
        return user;
    }

    @Override
    public boolean deleteById(Long id) {
        return users.remove(id) != null;
    }

    @Override
    public List<User> getAll() {
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> getById(Long id) {
        if (users.containsKey(id)) {
            User user = users.get(id);
            return Optional.of(User.builder()
                    .email(user.getEmail())
                    .id(user.getId())
                    .name(user.getName())
                    .build());
        }
        return Optional.empty();
    }
}
