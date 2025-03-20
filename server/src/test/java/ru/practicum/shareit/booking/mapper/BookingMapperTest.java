package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingMapperTest {
    @Test
    void toBookingDto() {
        User user = new User(1L, "sav", "sav@mail.ru");
        Item item = new Item(1L, "item", "description", true, user,
                null);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);
        Booking booking = new Booking(1L, item, start, end, user, BookingStatus.WAITING);
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        assertNotNull(bookingDto);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(bookingDto.getBooker().getName(), user.getName());
    }

    @Test
    void toBooking() {
        User user = new User(1L, "sav", "sav@mail.ru");
        Item item = new Item(1L, "item", "description", true, user,
                null);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);
        NewBookingDto newBookingDto = new NewBookingDto(item.getId(), start, end);
        Booking booking = BookingMapper.toBooking(newBookingDto, item, user);
        assertNotNull(booking);
        assertEquals(booking.getBooker(), user);
    }
}
