package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<ItemDto> itemDtos) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requester(request.getRequestor().getId())
                .items(itemDtos)
                .build(
                );
    }

    public static ItemRequest toItemRequest(ItemRequestDto request, User user) {
        return ItemRequest.builder()
                .description(request.getDescription())
                .requestor(user)
                .build();
    }
}