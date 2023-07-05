package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemService {
    List<ItemDtoWithBooking> getItems(Long ownerId, int page, int size);

    ItemDtoWithBooking getItemById(Long itemId, Long ownerId);

    ItemDto postItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(ItemDto itemDto, Long ownerId);

    void deleteItem(Long id);

    List<ItemDto> searchItems(String text, int page, int size);

    CommentDto postComment(Long itemId, Comment comment, Long ownerId);

    List<CommentDto> searchComments(Long itemId, Long authorId, String text);
}