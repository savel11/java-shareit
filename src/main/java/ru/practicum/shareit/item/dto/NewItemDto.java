package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
public class NewItemDto {
    @NotBlank(message = "Некорректный формат названия: название не может быть пустым или отсутствовать")
    private String name;
    @NotBlank(message = "Некорректный формат описания: описание не может быть пустым или отсутствовать")
    private String description;
    @NotNull(message = "Некорректный формат статуса: необходимо указать статус")
    private Boolean available;
    private ItemRequest request;
}
