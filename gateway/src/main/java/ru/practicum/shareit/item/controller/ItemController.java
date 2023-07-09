package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @Positive @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size) {
        return itemClient.getItems(ownerId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(
            @PathVariable Long id,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemClient.getItemById(id, ownerId);
    }

    @PostMapping
    public ResponseEntity<Object> postItem(@Valid @RequestBody ItemDto itemDto,
                                           @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemClient.postItem(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @PathVariable Long id,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemClient.updateItem(itemDto, id, ownerId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long id) {
        return itemClient.deleteItem(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam String text,
            @Positive @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size) {
        return itemClient.searchItems(ownerId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@PathVariable Long itemId,
                                              @Valid @RequestBody Comment comment,
                                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemClient.postComment(itemId, comment, ownerId);
    }
}