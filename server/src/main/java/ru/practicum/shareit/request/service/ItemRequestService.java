package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestDto> getItemRequests(Long from, Long size, Long requestorId);

    List<ItemRequestDto> getItemRequestsByRequestor(Long requestorId);

    ItemRequestDto getItemRequestById(Long requestId, Long requestorId);

    ItemRequestDto postItemRequest(ItemRequestDto itemRequestDto, Long ownerId);
}