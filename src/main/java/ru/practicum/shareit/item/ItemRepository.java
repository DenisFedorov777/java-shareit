package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    public Item addItem(Item item);

    Optional<Item> findById(Long id);

    void updateById(Item item, Long id);

    List<Item> findByUserId(Long userId);

    List<Item> findBySearch(String text);
}