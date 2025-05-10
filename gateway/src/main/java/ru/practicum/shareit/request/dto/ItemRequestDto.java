package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemRequestDto {
    @NotBlank(message = "Некорректный формат описания: описание не должно быть пустым.")
    @Size(max = 300, message = "Некорректный формат описания: описание не должно превышать 300 символов.")
    private String description;
}
