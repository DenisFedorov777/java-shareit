package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItemById(Long id);

    ItemDto create(ItemDto itemDto, Long id);

    ItemDto updateById(ItemDto itemDto, Long id, Long userId);

    List<ItemDto> getAllItemByUserId(Long userId);

    List<ItemDto> searchByText(String text);
}