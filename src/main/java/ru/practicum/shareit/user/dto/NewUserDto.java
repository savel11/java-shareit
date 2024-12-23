package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewUserDto {
    @NotBlank(message = "Некорректный формат логина: Логин не должен быть пустым.")
    private String name;
    @Email(regexp = ".+[@].+[\\.].+", message = "Некорректный формат электронной почты.")
    @NotBlank(message = "Некорректный формат почты: Почта не должена быть пустой.")
    private String email;
}
