package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.exception.InvalidFormatException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;


import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;
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
    void createItemNotExistingUser() {
        NewItemDto newItem = new NewItemDto("name", "descriptional", true, null);
        assertThrows(NotFoundException.class, () -> itemService.create(newItem, 100L));
    }

    @Test
    void createWithNotExistingRequest() {
        NewItemDto newItem = new NewItemDto("name", "descriptional", true, 100L);
        assertThrows(InvalidFormatException.class, () -> itemService.create(newItem, userDto.getId()));
    }

    @Test
    void update() {
        NewItemDto newItem = new NewItemDto("name", "descriptional", true, null);
        ItemDto itemOld = itemService.create(newItem, userDto.getId());
        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("new");
        updateItemDto.setDescription("newD");

        ItemDto item = itemService.update(updateItemDto, userDto.getId(), itemOld.getId());
        TypedQuery<Item> query = em.createQuery("SELECT i from Item as i where i.id = :id", Item.class);
        Item result = query.setParameter("id", item.getId()).getSingleResult();

        assertNotNull(result);
        assertEquals(itemOld.getId(), result.getId());
        assertEquals(updateItemDto.getName(), result.getName());
        assertEquals(updateItemDto.getDescription(), result.getDescription());
    }

    @Test
    void updateNotOwner() {
        NewItemDto newItem = new NewItemDto("name", "descriptional", true, null);
        ItemDto itemOld = itemService.create(newItem, userDto.getId());
        UserDto notOwner = userService.create(new NewUserDto("Save", "say.losev@gmail.com"));
        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("new");
        updateItemDto.setDescription("newD");
        assertThrows(NotOwnerException.class, () -> itemService.update(updateItemDto, notOwner.getId(), itemOld.getId()));
    }

    @Test
    void updateNotExistingItem() {
        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("new");
        updateItemDto.setDescription("newD");
        assertThrows(NotFoundException.class, () -> itemService.update(updateItemDto, userDto.getId(), 100L));
    }

    @Test
    void updateNotExistingUser() {
        NewItemDto newItem = new NewItemDto("name", "descriptional", true, null);
        ItemDto itemOld = itemService.create(newItem, userDto.getId());
        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("new");
        updateItemDto.setDescription("newD");
        assertThrows(NotFoundException.class, () -> itemService.update(updateItemDto, 100L, itemOld.getId()));
    }

    @Test
    void getItemById() {
        NewItemDto newItem = new NewItemDto("name", "descriptional", true, null);
        ItemDto createdItem = itemService.create(newItem, userDto.getId());
        ItemDtoWithDate item = itemService.getItemById(createdItem.getId());
        assertThat(item, notNullValue());
        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(newItem.getName()));
        assertThat(item.getDescription(), equalTo(newItem.getDescription()));
    }

    @Test
    void getNotExistingItem() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(100L));
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

    @Test
    void createComment() throws InterruptedException {
        NewItemDto newItem = new NewItemDto("name", "descriptional", true, null);
        ItemDto item = itemService.create(newItem, userDto.getId());
        NewCommentDto newComm = new NewCommentDto("cool");
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusNanos(100000000);
        NewBookingDto newBookingDto = new NewBookingDto(item.getId(), start, end);
        BookingDto bookingDto = bookingService.create(newBookingDto, userDto.getId());
        bookingService.approve(bookingDto.getId(), userDto.getId(), true);
        Thread.sleep(1000);
        CommentDto commentDto = itemService.createComment(newComm, item.getId(), userDto.getId());
        TypedQuery<Comment> query = em.createQuery("select c from Comment as c where c.text = :text", Comment.class);
        Comment result = query.setParameter("text", commentDto.getText()).getSingleResult();
        assertNotNull(result);
        assertEquals(commentDto.getId(), result.getId());
        assertEquals(commentDto.getText(), result.getText());
    }
}
