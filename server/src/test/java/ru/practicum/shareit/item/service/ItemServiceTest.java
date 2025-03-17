package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;


import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final EntityManager em;

    private UserDto userDto;

    @BeforeEach
    void setUserDto() {
        userDto = userService.create(new NewUserDto("Saveliy", "saveliy.losev@gmail.com"));
    }


    @Test
    void createItemByUser() {
        NewItemDto newItem = new NewItemDto("name", "descriptional", true, null);
        ItemDto item = itemService.create(newItem, userDto.getId());

        TypedQuery<Item> query = em.createQuery("select item from Item as item where item.name = :name", Item.class);
        Item itemQ = query.setParameter("name", item.getName()).getSingleResult();

        assertThat(itemQ.getId(), notNullValue());
        assertThat(itemQ.getId(), equalTo(item.getId()));
        assertThat(itemQ.getName(), equalTo(item.getName()));
        assertThat(itemQ.getDescription(), equalTo(item.getDescription()));
    }

    @Test
    void createItemWithRequest() {
        NewItemRequestDto request = new NewItemRequestDto("descr");
        ItemRequestDto requestDto = itemRequestService.create(request, userDto.getId());
        NewItemDto newItem = new NewItemDto("name", "descriptional", true, requestDto.getId());
        ItemDto item = itemService.create(newItem, userDto.getId());

        TypedQuery<Item> query = em.createQuery("select item from Item as item where item.name = :name", Item.class);
        Item itemQ = query.setParameter("name", item.getName()).getSingleResult();

        assertThat(itemQ.getId(), notNullValue());
        assertThat(itemQ.getId(), equalTo(item.getId()));
        assertThat(itemQ.getName(), equalTo(item.getName()));
        assertThat(itemQ.getDescription(), equalTo(item.getDescription()));
        assertThat(itemQ.getRequest().getId(), equalTo(item.getRequestId()));
    }

    @Test
    void getItemById() {
        NewItemDto newItem = new NewItemDto("name", "descriptional", true, null);
        ItemDto item = itemService.create(newItem, userDto.getId());

        assertThat(item, notNullValue());
        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(newItem.getName()));
        assertThat(item.getDescription(), equalTo(newItem.getDescription()));
    }

    @Test
    void search() {
        NewItemDto newItem = new NewItemDto("name", "descriptional", true, null);
        ItemDto item = itemService.create(newItem, userDto.getId());
        List<ItemDto> items = itemService.searchItems("name");

        assertThat(items, notNullValue());
        assertThat(1, equalTo(items.size()));
        assertThat(item.getId(), equalTo(items.getFirst().getId()));
    }

    @Test
    void getItemsByOwnerId() {
        NewItemDto newItem = new NewItemDto("name", "descriptional", true, null);
        ItemDto item = itemService.create(newItem, userDto.getId());
        List<ItemDtoWithDate> items = itemService.getItems(userDto.getId());

        assertThat(items, notNullValue());
        assertThat(1, equalTo(items.size()));
        assertThat(item.getId(), equalTo(items.getFirst().getId()));
    }
}
