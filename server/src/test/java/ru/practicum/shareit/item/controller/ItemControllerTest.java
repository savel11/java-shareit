package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemDtoWithDate dto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        dto = new ItemDtoWithDate(1L, "name", "description", true, null, null,
                null, null, null);
    }

    @Test
    void getItemsByOwnerId() throws Exception {
        when(itemService.getItems(1L)).thenReturn(List.of(dto));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void search() throws Exception {
        when(itemService.searchItems(anyString())).thenReturn(List.of(
                new ItemDto(1L, "name", "description", true, null, null)));
        mvc.perform(get("/items/search?text=text")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(1L)).thenReturn(dto);
        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(dto.getName())));
    }


    @Test
    void createItemByUser() throws Exception {
        NewItemDto newItem = new NewItemDto("name", "descriptional", true, null);
        ItemDto item = new ItemDto(1L, "name", "descriptional", true, 1L, null);
        when(itemService.create(newItem, 1L)).thenReturn(item);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(newItem.getName())))
                .andExpect(jsonPath("$.description", is(newItem.getDescription())))
                .andExpect(jsonPath("$.ownerId", is(1)));
    }

    @Test
    void createComment() throws Exception {
        NewCommentDto newC = new NewCommentDto("comment");
        CommentDto comm = new CommentDto(
                1L,
                "comment",
                null,
                null
        );
        when(itemService.createComment(newC, 1L, 1L)).thenReturn(comm);
        mvc.perform(post("/items/1/comment").header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(comm))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(newC.getText())));
    }

    @Test
    void updateUserItem() throws Exception {
        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("new");
        updateItemDto.setDescription("newD");
        ItemDto item = new ItemDto(1L, "new", "newD", null, 1L,null);
        when(itemService.update(updateItemDto, 1L, 1L)).thenReturn(item);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(item)));
    }
}

