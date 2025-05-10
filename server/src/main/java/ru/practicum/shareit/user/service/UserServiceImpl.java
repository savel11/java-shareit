package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return UserMapper.toUserDto(optionalUser.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto create(NewUserDto newUserDto) {
        log.info("Создание нового пользователя");
        log.trace("Проверка на дубликат email: " + newUserDto.getEmail());
        if (isDuplicatedEmailForCreate(newUserDto.getEmail())) {
            log.warn("Пользователь не был создан: Пользователь с таким email уже существует");
            throw new DuplicatedDataException("Почта уже используется");
        }
        log.trace("Проверка пройдена!");
        log.info("Пользоваетль успешно создан!");
        User user = userRepository.save(UserMapper.toUser(newUserDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Удаление пользователя с id = " + id);
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            log.warn("Пользователь не был удален: пользователь с указанным id не существует");
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        userRepository.delete(userOpt.get());
        log.info("Пользоваетль успешно удален!");
    }

    @Override
    @Transactional
    public UserDto update(UpdateUserDto updateUserDto, Long id) {
        log.info("Обновление пользователя с id = " + id);
        Optional<User> optionalUser = userRepository.findById(id);
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
        User updateUser = updateFields(user, updateUserDto);
        userRepository.save(updateUser);
        return UserMapper.toUserDto(updateUser);
    }

    private boolean isDuplicatedEmailForCreate(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.isPresent();
    }

    private boolean isDuplicatedEmailForUpdate(String email, User user) {
        Optional<User> userOpt = userRepository.findByEmailAndIdNot(email, user.getId());
        return userOpt.isPresent();
    }

    private User updateFields(User user, UpdateUserDto updateUserDto) {
        if (updateUserDto.getEmail() != null) {
            user.setEmail(updateUserDto.getEmail());
        }
        if (updateUserDto.getName() != null) {
            user.setName(updateUserDto.getName());
        }
        return user;
    }
}
