package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ItemMappeerTest {

    @Test
    void toItemDtoTest() {
        Item item = new Item(1L, "item", "decsc", true, null, null);
        User user = new User(1L, "Sav", "sv@mail.ru");
        item.setOwner(user);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void toItem() {
        NewItemDto itemDto = new NewItemDto("item", "description", true, null);
        User user = new User(1L, "Sav", "sv@mail.ru");
        Item item = ItemMapper.toItem(itemDto, user, null);
        assertNotNull(item);
        assertEquals(item.getName(), itemDto.getName());
        assertNull(item.getRequest());
    }

    @Test
    void toItemDtoWithDate() {
        User user = new User(1L, "Sav", "sv@mail.ru");
        Item item = new Item(1L, "item", "decsc", true, user, null);
        List<Comment> comm = Collections.emptyList();
        LocalDateTime start = LocalDateTime.now().minus(Period.ofDays(10));
        LocalDateTime end = LocalDateTime.now().minus(Period.ofDays(5));
        BookingShort first = new BookingShort(1L, start);
        BookingShort second = new BookingShort(1L, end);
        ItemDtoWithDate itemDto = ItemMapper.itemDtoWithDate(item, first, second, comm);
        assertNotNull(itemDto);
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(start, itemDto.getNextBooking());
        assertEquals(end, itemDto.getLastBooking());
    }

    @Test
    void toItemDtoShort() {
        Item item = new Item(1L, "item", "decsc", true, null, null);
        User user = new User(1L, "Sav", "sv@mail.ru");
        item.setOwner(user);
        ItemDtoShort itemDto = ItemMapper.toItemDtoShort(item);
        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
    }
}
