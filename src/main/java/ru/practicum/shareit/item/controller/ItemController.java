package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
@RestController
public class ItemController {

    private static final String X_HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemService service;

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable("id") Long id) {
        log.info("Получение вещи по идентификатору");
        return service.getItemById(id);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader(X_HEADER_USER_ID) Long userId) {
        log.info("Все вещи пользователя");
        return service.getAllItemByUserId(userId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(X_HEADER_USER_ID) Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Добавление вещи");
        return service.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(X_HEADER_USER_ID) Long userId, @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Добавить данные некоторой вещи");
        return service.updateById(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam String text) {
        log.info("Поиск");
        return service.searchByText(text);
    }
}