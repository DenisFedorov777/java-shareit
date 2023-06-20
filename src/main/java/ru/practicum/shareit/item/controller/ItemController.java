package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;
    private final ItemMapper mapper;
    private final CommentMapper commentMapper;

    @GetMapping
    public List<ItemDtoWithBooking> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItemById(@PathVariable("itemId") Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getItemById(itemId, userId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Validated({Create.class})
    @RequestBody ItemDto itemDto) {
        Item item = mapper.toItem(itemDto);
        return mapper.toItemDto(service.createItem(userId, item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto, @PathVariable("itemId") Long itemId) {
        Item item = mapper.toItem(itemDto);
        return mapper.toItemDto(service.updateItem(userId, item, itemId));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemForText(@RequestParam("text") String text) {
        return service.searchItemForText(text).stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody CommentDtoRequest commentDtoRequest,
                                    @PathVariable("itemId") Long itemId) {
        Comment comment = commentMapper.toComment(commentDtoRequest);
        return commentMapper.toCommentDto(service.createComment(userId, comment, itemId));
    }
}