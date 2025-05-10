package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(NewItemRequestDto newItemRequestDto, Long userId);

    ItemRequestDto getItemRequestById(Long userId, Long itemRequestId);

    List<ItemRequestDto> getAllItemRequest(Long userId);

    List<ItemRequestDto> getAllByUserId(Long userId);

}
