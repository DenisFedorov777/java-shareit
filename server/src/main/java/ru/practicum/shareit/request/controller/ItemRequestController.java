package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto postItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                          @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return itemRequestService.postItemRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestsByRequestor(
            @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return itemRequestService.getItemRequestsByRequestor(requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId,
                                             @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return itemRequestService.getItemRequestById(requestId, requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getItemRequests(@RequestParam(defaultValue = "0") Long from,
                                                @RequestParam(defaultValue = "10") Long size,
                                                @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return itemRequestService.getItemRequests(from, size, requestorId);
    }
}