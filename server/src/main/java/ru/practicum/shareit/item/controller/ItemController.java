package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoWithBooking> getItems(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return itemService.getItems(ownerId, page, size);
    }

    @GetMapping("/{id}")
    public ItemDtoWithBooking getItemById(
            @PathVariable Long id,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getItemById(id, ownerId);
    }

    @PostMapping
    public ItemDto postItem(@RequestBody ItemDto itemDto,
                            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.postItem(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long id,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        itemDto.setId(id);
        return itemService.updateItem(itemDto, ownerId);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return itemService.searchItems(text, page, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@PathVariable Long itemId,
                                  @RequestBody Comment comment,
                                  @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.postComment(itemId, comment, ownerId);
    }
}