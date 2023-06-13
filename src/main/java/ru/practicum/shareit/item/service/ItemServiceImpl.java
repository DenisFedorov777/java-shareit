package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getItemById(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Такой вещи нет"));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.addItem(newItem));
    }

    @Override
    public ItemDto updateById(ItemDto itemDto, Long itemId, Long userId) {
        Item newItem = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Такой вещи нет!"));
        if (!Objects.equals(newItem.getOwner().getId(), userId)) {
            throw new NotFoundException("Это не тот владелец, ищите другого");
        }
        if (StringUtils.hasLength(itemDto.getName())) {
            newItem.setName(itemDto.getName());
        }
        if (StringUtils.hasLength(itemDto.getDescription())) {
            newItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            newItem.setAvailable(itemDto.getAvailable());
        }
        itemRepository.updateById(newItem, itemId);
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public List<ItemDto> getAllItemByUserId(Long userId) {
        List<Item> items = itemRepository.findByUserId(userId);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        if (!StringUtils.hasLength(text)) {
            return Collections.emptyList();
        }
        return itemRepository.findBySearch(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}