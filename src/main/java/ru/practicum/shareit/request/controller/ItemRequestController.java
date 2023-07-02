package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto postItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                          @RequestHeader(value = "X-Sharer-User-Id",
                                                  required = true) Long requestorId) {
        return itemRequestService.postItemRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestsByRequestor(
            @RequestHeader(value = "X-Sharer-User-Id", required = true) Long requestorId) {
        return itemRequestService.getItemRequestsByRequestor(requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId,
                                             @RequestHeader(value = "X-Sharer-User-Id", required = true) Long requestorId) {
        return itemRequestService.getItemRequestById(requestId, requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getItemRequests(@PositiveOrZero @RequestParam(value = "from", required = false) Long from,
                                                @Positive @RequestParam(value = "size", required = false) Long size,
                                                @RequestHeader(value = "X-Sharer-User-Id", required = true) Long requestorId) {
        return itemRequestService.getItemRequests(from, size, requestorId);
    }
}