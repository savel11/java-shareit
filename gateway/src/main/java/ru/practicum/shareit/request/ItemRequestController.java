package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание запроса: " + itemRequestDto + "  от пользоваетлся с id = " + userId);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("requestId") Long requestId) {
        log.info("Получен запрос на просмотр запроса с id: " + requestId + "  от пользоваетлся с id = " + userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на просмотр всех запросов от пользоваетлся с id = " + userId);
        return itemRequestClient.getAllItemRequest(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getMyItemsRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на просмотр своих запросов от пользоваетлся с id = " + userId);
        return itemRequestClient.getMyItemsRequests(userId);
    }
}
