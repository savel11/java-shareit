package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(NewBookingDto newBookingDto, Long bookerId);

    BookingDto approve(Long bookingId, Long userId, Boolean approved);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getAllBooking(Long userId, String state);

    List<BookingDto> getAllBookingOwner(Long userId, String state);
}
