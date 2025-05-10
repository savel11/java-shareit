package ru.practicum.shareit.user.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class NewUserDto {
    private String name;
    private String email;
}
