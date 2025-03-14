package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(NewItemDto newItemDto, Long ownerId);

    ItemDto update(UpdateItemDto updateItemDto, Long ownerId, Long itemId);

    ItemDtoWithDate getItemById(Long itemId);

    List<ItemDtoWithDate> getItems(Long ownerId);

    List<ItemDto> searchItems(String text);

    CommentDto createComment(NewCommentDto newCommentDto, Long itemId, Long userId);
}
