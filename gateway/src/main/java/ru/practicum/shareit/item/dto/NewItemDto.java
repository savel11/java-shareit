package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewItemDto {
    @NotBlank(message = "Некорректный формат названия: название не может быть пустым или отсутствовать")
    @Size(max = 30, message = "Некорректный формат название: Название не должно превышать 30 символов.")
    private String name;
    @NotBlank(message = "Некорректный формат описания: описание не может быть пустым или отсутствовать")
    @Size(max = 200, message = "Некорректный формат описания: Описания не должно превышать 200 символов.")
    private String description;
    @NotNull(message = "Некорректный формат статуса: необходимо указать статус")
    private Boolean available;
    private Long requestId;
}
