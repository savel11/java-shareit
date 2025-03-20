package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommentMapperTest {
    @Test
    void toCommentDto() {
        Item item = new Item(1L, "item", "decsc", true, null, null);
        User user = new User(1L, "Sav", "sv@mail.ru");
        item.setOwner(user);
        Comment comment = new Comment(1L, "comm", item, user, LocalDateTime.now());
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(user.getName(), commentDto.getAuthorName());
    }

    @Test
    void toComment() {
        Item item = new Item(1L, "item", "decsc", true, null, null);
        User user = new User(1L, "Sav", "sv@mail.ru");
        item.setOwner(user);
        NewCommentDto newCommentDto = new NewCommentDto("cool");
        Comment comment = CommentMapper.toComment(newCommentDto, user, item);
        assertNotNull(comment);
        assertEquals(newCommentDto.getText(), comment.getText());
        assertEquals(user, comment.getAuthor());
        assertEquals(item, comment.getItem());
    }
}
