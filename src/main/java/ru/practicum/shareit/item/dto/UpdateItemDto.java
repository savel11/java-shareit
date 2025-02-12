package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateItemDto {
    @Size(max = 30, message = "Некорректный формат название: Название не должно превышать 30 символов.")
    private String name;
    @Size(max = 200, message = "Некорректный формат описания: Описания не должно превышать 200 символов.")
    private String description;
    private Boolean available;
}
