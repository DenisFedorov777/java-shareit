package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        ItemRequest request = item.getRequest();
        if (request != null) {
            itemDto.setRequestId(request.getId());
        }
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(Item item) {
        ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking();
        itemDtoWithBooking.setId(item.getId());
        itemDtoWithBooking.setName(item.getName());
        itemDtoWithBooking.setDescription(item.getDescription());
        itemDtoWithBooking.setAvailable(item.getAvailable());
        return itemDtoWithBooking;
    }
}