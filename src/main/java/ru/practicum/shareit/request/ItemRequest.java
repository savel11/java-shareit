package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
public class ItemRequest {
    private Long id;
    private String description;
    private User userRequest;
    private LocalDateTime created;
}
