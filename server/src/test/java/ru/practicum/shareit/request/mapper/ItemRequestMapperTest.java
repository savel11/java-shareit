package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ItemRequestMapperTest {
    @Test
    void toItemRequest() {
        User user = new User(1L, "Sav", "sv@mail.ru");
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto("descr");
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(newItemRequestDto, user);
        assertNotNull(itemRequest);
        assertEquals(itemRequest.getDescription(), newItemRequestDto.getDescription());
        assertEquals(itemRequest.getUserRequest(), user);
    }

    @Test
    void toItemRequestDto() {
        User user = new User(1L, "Sav", "sv@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "desct", user, LocalDateTime.now());
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, Collections.emptyList());
        assertNotNull(itemRequestDto);
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDto.getUserRequest().getName(), user.getName());
    }
}
