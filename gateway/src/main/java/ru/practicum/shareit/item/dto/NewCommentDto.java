package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCommentDto {
    @Size(max = 500, message = "Некорректный формат комментария: комментарий не должен превышать 500 символов.")
    private String text;
}
