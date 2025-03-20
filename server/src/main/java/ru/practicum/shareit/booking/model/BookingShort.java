package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class BookingShort {
    private Long itemId;
    private LocalDateTime dateTime;
}
