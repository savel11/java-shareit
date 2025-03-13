package ru.practicum.shareit.user.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.DuplicatedDataException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    @Autowired
    @Qualifier("InMemoryUserRepository")
    private UserRepository userRepository;

    public UserDto getUserById(Long id) {
        Optional<User> optionalUser = userRepository.getById(id);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return UserMapper.toUserDto(optionalUser.get());
    }

    public List<UserDto> getAll() {
        return userRepository.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto create(NewUserDto newUserDto) {
        log.info("Создание нового пользователя");
        log.trace("Проверка на дубликат email: " + newUserDto.getEmail());
        if (isDuplicatedEmailForCreate(newUserDto.getEmail())) {
            log.warn("Пользователь не был создан: Пользователь с таким email уже существует");
            throw new DuplicatedDataException("Почта уже используется");
        }
        log.trace("Проверка пройдена!");
        log.info("Пользоваетль успешно создан!");
        User user = userRepository.create(UserMapper.toUser(newUserDto));
        return UserMapper.toUserDto(user);
    }

    public void delete(Long id) {
        log.info("Удаление пользователя с id = " + id);
        if (!userRepository.deleteById(id)) {
            log.warn("Пользователь не был удален: пользователь с указанным id не существует");
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        log.info("Пользоваетль успешно удален!");
    }

    public UserDto update(UpdateUserDto updateUserDto, Long id) {
        log.info("Обновление пользователя с id = " + id);
        Optional<User> optionalUser = userRepository.getById(id);
        log.trace("Проверка сущестование пользователя");
        if (optionalUser.isEmpty()) {
            log.warn("Пользователя с id = " + id + " не существует");
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        log.trace("Проверка пройдена");
        log.trace("Проверяем свободно ли новая почта");
        if (isDuplicatedEmailForUpdate(updateUserDto.getEmail(), optionalUser.get())) {
            log.warn("Пользователь не был обновлен: Пользователь с таким email уже существует");
            throw new DuplicatedDataException("Почта уже используется");
        }
        log.info("Данные пользователя успешно обновленны");
        User user = optionalUser.get();
        return updateFields(user, updateUserDto);
    }

    private boolean isDuplicatedEmailForCreate(String email) {
        return userRepository.getAll().stream().map(User::getEmail).anyMatch(u -> u.equals(email));
    }

    private boolean isDuplicatedEmailForUpdate(String email, User user) {
        return userRepository.getAll().stream().filter(u -> !u.equals(user)).anyMatch(u -> u.getEmail().equals(email));
    }

    private UserDto updateFields(User user, UpdateUserDto updateUserDto) {
        if (updateUserDto.getEmail() != null) {
            user.setEmail(updateUserDto.getEmail());
        }
        if (updateUserDto.getName() != null) {
            user.setName(updateUserDto.getName());
        }
        return UserMapper.toUserDto(user);
    }
}
