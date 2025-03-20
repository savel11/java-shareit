package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(Long itemId);

    @Query("select comm " +
            "from Comment As comm " +
            "JOIN  comm.item AS i " +
            "JOIN i.owner AS ow " +
            "where ow.id = ?1")
    List<Comment> getCommentsByOwnerID(Long ownerId);
}
