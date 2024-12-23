package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OwnerItemDto {
    private String name;
    private String description;
}
