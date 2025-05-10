package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingShort;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwnerId(item.getOwner().getId());
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }


    public static Item toItem(NewItemDto item, User owner, ItemRequest itemRequest) {
        Item newItem = new Item();
        newItem.setOwner(owner);
        newItem.setDescription(item.getDescription());
        newItem.setAvailable(item.getAvailable());
        newItem.setName(item.getName());
        newItem.setRequest(itemRequest);
        return newItem;
    }


    public static ItemDtoWithDate itemDtoWithDate(Item item, BookingShort first, BookingShort last, List<Comment> comments) {
        List<CommentDto> comm = comments.stream().map(CommentMapper::toCommentDto).toList();
        LocalDateTime start = null;
        if (first != null) {
            start = first.getDateTime();
        }
        LocalDateTime end = null;
        if (last != null) {
            end = last.getDateTime();
        }
        return ItemDtoWithDate.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .request(item.getRequest())
                .nextBooking(start)
                .lastBooking(end)
                .comments(comm)
                .build();
    }

    public static ItemDtoShort toItemDtoShort(Item item) {
        return ItemDtoShort.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }
}
