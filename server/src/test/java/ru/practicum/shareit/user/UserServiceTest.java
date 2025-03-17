package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final EntityManager em;
    private final UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = userService.create(new NewUserDto("Saveliy", "saveliy.losev@gmail.com"));
    }

    @Test
    void getAll() {
        List<UserDto> users = userService.getAll();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(userDto.getId(), users.getFirst().getId());
    }

    @Test
    void getByIdFailsWithWrongId() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(50L));
    }

    @Test
    void getById() {
        UserDto user = userService.getUserById(userDto.getId());

        assertNotNull(user);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
    }

    @Test
    void create() {
        UserDto user = userService.create(new NewUserDto("A","ema@mail.ru"));
        TypedQuery<User> query = em.createQuery("SELECT us from User as us where u.email = :email", User.class);
        User result = query.setParameter("email", user.getEmail()).getSingleResult();

        assertNotNull(result);
        assertEquals(result.getName(), user.getName());
        assertEquals(result.getEmail(), user.getEmail());
    }
}
