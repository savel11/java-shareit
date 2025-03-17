package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDtoShort {
    private Long id;
    private String name;
    private Long ownerId;
}
