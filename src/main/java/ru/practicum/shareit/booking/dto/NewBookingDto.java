package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewBookingDto {
    private Long itemId;
    @FutureOrPresent(message = "Некорректный формат даты: дата не может быть в прошлом")
    private LocalDateTime start;
    @Future(message = "Некорректный формат даты: дата не может быть в прошлом")
    private LocalDateTime end;
}
