package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    User update(User user);

    boolean deleteById(Long id);

    List<User> getAll();

    Optional<User> getById(Long id);
}
