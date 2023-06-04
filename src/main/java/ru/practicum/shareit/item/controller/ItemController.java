package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
//@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@RestController
public class ItemController {

    private static final String X_HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService service;

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable("id") Long id) {
        return service.getItemById(id);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader(X_HEADER_USER_ID) Long userId) {
        return service.getAllItemByUserId(userId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(X_HEADER_USER_ID) Long userId, @Valid @RequestBody ItemDto itemDto) {
        return service.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(X_HEADER_USER_ID) Long userId, @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        return service.updateById(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam String text) {
        return service.searchByText(text);
    }
}