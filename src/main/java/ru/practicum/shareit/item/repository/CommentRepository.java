package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.item.id = ?1")
    List<Comment> findByItemId(Long itemId);

    @Query("SELECT c FROM Comment c WHERE c.author.id = ?1")
    List<Comment> findByAuthorId(Long authorId);

    @Query("SELECT c FROM Comment c WHERE lower(c.text) LIKE %?1%")
    List<Comment> findByTextContainingIgnoreCase(String text);
}