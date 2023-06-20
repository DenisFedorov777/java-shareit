package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDtoWithBooking> getAllItems(Long userId);

    ItemDtoWithBooking getItemById(Long itemId, Long userId);

    Item createItem(Long userId, Item item);

    Item updateItem(Long userId, Item item, Long itemId);

    List<Item> searchItemForText(String text);

    Comment createComment(Long userId, Comment comment, Long itemId);
}