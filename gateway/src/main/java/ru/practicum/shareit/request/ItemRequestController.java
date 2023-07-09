package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> postItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                  @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return itemRequestClient.postItemRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByRequestor(
            @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return itemRequestClient.getItemRequestsByRequestor(requestorId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Long requestId,
                                                     @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return itemRequestClient.getItemRequestById(requestId, requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequests(@PositiveOrZero @RequestParam(defaultValue = "0") Long from,
                                                  @Positive @RequestParam(defaultValue = "10") Long size,
                                                  @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return itemRequestClient.getItemRequests(from, size, requestorId);
    }
}