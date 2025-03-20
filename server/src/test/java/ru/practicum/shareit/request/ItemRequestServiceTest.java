package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceTest {
    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    private UserDto userDto;

    private ItemRequestDto itemRequestDto;


    @BeforeEach
    void setUp() {
        userDto = userService.create(new NewUserDto("Saveliy", "saveliy.losev@gmail.com"));
        itemRequestDto = itemRequestService.create(new NewItemRequestDto("request"), userDto.getId());
    }

    @Test
    void create() {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto("req");
        ItemRequestDto itemReq = itemRequestService.create(newItemRequestDto, userDto.getId());
        TypedQuery<ItemRequest> query = em.createQuery("SELECT it from ItemRequest as it where" +
                " it.description = :description", ItemRequest.class);
        ItemRequest result = query.setParameter("description", itemReq.getDescription()).getSingleResult();
        assertNotNull(result);
        assertEquals(itemReq.getId(), result.getId());
        assertEquals(itemReq.getDescription(), result.getDescription());
    }

    @Test
    void getById() {
        ItemRequestDto item = itemRequestService.getItemRequestById(userDto.getId(), itemRequestDto.getId());

        assertNotNull(item);
        assertEquals(itemRequestDto.getId(), item.getId());
    }

    @Test
    void getAll() {
        UserDto newW = userService.create(new NewUserDto("Sa", "sav.losev@gmail.com"));
        List<ItemRequestDto> items = itemRequestService.getAllItemRequest(newW.getId());

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(itemRequestDto.getId(), items.getFirst().getId());
    }

    @Test
    void getAllByUserId() {
        List<ItemRequestDto> items = itemRequestService.getAllByUserId(userDto.getId());

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(itemRequestDto.getId(), items.getFirst().getId());
    }
}
