package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto createRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemRequestDto requestDto) {
        ItemRequest itemRequest = service.create(requestDto, userId);
        return service.getRequestDto(itemRequest);
    }

    @GetMapping
    public List<ItemRequestDto> findRequestByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemRequest> itemRequests = service.getRequestByUser(userId);
        return itemRequests.stream().map(service::getRequestDto).collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        ItemRequest itemRequest = service.getItemRequest(requestId, userId);
        return service.getRequestDto(itemRequest);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        Page<ItemRequest> allRequests = service.getAllRequests(userId, from, size);
        return allRequests.stream().map(service::getRequestDto).collect(Collectors.toList());
    }
}
