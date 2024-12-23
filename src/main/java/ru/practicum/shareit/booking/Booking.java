package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
public class Booking {
    private Long id;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;
    private User booker;
    private BookingStatus status;
}
