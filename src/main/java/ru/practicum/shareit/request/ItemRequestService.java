package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;

import java.util.List;

public interface ItemRequestService {

    ItemRequest create(ItemRequestDto requestDto, Long userId);

    ItemRequestDto getRequestDto(ItemRequest request);

    List<ItemRequest> getRequestByUser(Long userId);

    Page<ItemRequest> getAllRequests(Long userId, int from, int size);

    ItemRequest getItemRequest(Long requestId, Long userId);
}