package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;

import java.util.List;

@Component
public interface ItemRequestService {

    List<ItemRequestDto> getItemRequests(Long from, Long size, Long requestorId);

    List<ItemRequestDto> getItemRequestsByRequestor(Long requestorId);

    ItemRequestDto getItemRequestById(Long requestId, Long requestorId);

    ItemRequestDto postItemRequest(ItemRequestDto itemRequestDto, Long ownerId);
}