package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserServiceImpl service;

    @Override
    public ItemDto getItemById(Long id) {
        Item item = repository.findById(id).orElseThrow(() -> new NotFoundException("Такой вещи нет"));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = service.findUserById(userId);
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(user);
        return ItemMapper.toItemDto(repository.addItem(newItem));
    }

    @Override
    public ItemDto updateById(ItemDto itemDto, Long id, Long userId) {
        Item newItem = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Такой вещи нет!"));
        if (StringUtils.hasLength(itemDto.getName())) {
            newItem.setName(itemDto.getName());
        }
        if (!Objects.equals(newItem.getOwner().getId(), userId)) {
            throw new NotFoundException("Это не тот владелец, ищите другого");
        }
        if (StringUtils.hasLength(itemDto.getDescription())) {
            newItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            newItem.setAvailable(itemDto.getAvailable());
        }
        repository.updateById(newItem, id);
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public List<ItemDto> getAllItemByUserId(Long userId) {
        List<Item> items = repository.findByUserId(userId);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        if (!StringUtils.hasLength(text)) {
            return Collections.emptyList();
        }
        return repository.findBySearch(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}