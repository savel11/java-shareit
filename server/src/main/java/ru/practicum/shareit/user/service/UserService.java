package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long id);

    List<UserDto> getAll();

    UserDto create(NewUserDto newUserDto);

    void delete(Long id);

    UserDto update(UpdateUserDto updateUserDto, Long id);
}
