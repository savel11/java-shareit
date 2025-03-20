package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody final NewItemDto itemDto) {
        log.info("Получен запрос на создание вещи: " + itemDto + " пользователем с id = " + userId);
        return itemClient.create(itemDto, userId);
    }

    @ResponseBody
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody UpdateItemDto itemDto,
                                         @PathVariable Long itemId) {
        log.info("Получен запрос на обновление данных о вещи с id = " + itemId + " пользователем с id = " + userId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получен запрос на просмотр данных о вещи с id = " + itemId + " пользователем с id = " + userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на просмотр своих вещи пользователем с id = " + userId);
        return itemClient.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(name = "text") String text) {
        log.info("Получен запрос на поиск вещей содержащих: " + text + " пользователем с id = " + userId);
        return itemClient.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                                @Valid @RequestBody NewCommentDto comment) {
        log.info("Получен запрос на добавление комментария: " + comment + " пользователем с id = " + userId);
        return itemClient.createComment(comment, itemId, userId);
    }
}
