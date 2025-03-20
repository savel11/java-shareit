package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @ResponseBody
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос на создания пользователя: " + userDto);
        return userClient.create(userDto);
    }

    @ResponseBody
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Valid @RequestBody UpdateUserDto userDto, @PathVariable Long userId) {
        log.info("Получен запрос на обновление данных о пользователи с id: " + userId);
        return userClient.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Получен запрос на получение данных о пользователе с id: " + userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Получен запрос на получение данных о всех пользователях");
        return userClient.getAll();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользоваетля с id = " + userId);
        return userClient.delete(userId);
    }
}
