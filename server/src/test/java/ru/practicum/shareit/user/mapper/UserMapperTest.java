package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {
    @Test
    void toUser() {
        NewUserDto newUserDto = new NewUserDto("Savel", "sav@gmail.com");
        User user = UserMapper.toUser(newUserDto);
        assertNotNull(user);
        assertEquals(newUserDto.getName(), user.getName());
        assertEquals(newUserDto.getEmail(), user.getEmail());
    }

    @Test
    void toUserDto() {
        User user = new User(1L,"Savel", "sav@gmail.com");
        UserDto userDto = UserMapper.toUserDto(user);
        assertNotNull(userDto);
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getId(), userDto.getId());
    }
}
