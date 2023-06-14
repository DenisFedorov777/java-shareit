package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private volatile Long id = 1L;
    private final Map<Long, Item> dataItems = new HashMap<>();
    private final Map<Long, List<Item>> itemsByUser = new HashMap<>();

    private synchronized Long generatedId() {
        return id++;
    }

    @Override
    public Item addItem(Item item) {
        item.setId(generatedId());
        dataItems.put(item.getId(), item);
        Long userId = item.getOwner().getId();
        if (!itemsByUser.containsKey(userId)) {
            itemsByUser.put(userId, new ArrayList<>());
        }
        itemsByUser.get(userId).add(item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(dataItems.get(id));
    }

    @Override
    public void updateById(Item item, Long id) {
        dataItems.put(id, item);
        Long userId = item.getOwner().getId();
        itemsByUser.get(userId).remove(item);
        itemsByUser.get(userId).add(item);
    }

    @Override
    public List<Item> findByUserId(Long userId) {
        return itemsByUser.get(userId);
    }

    @Override
    public List<Item> findBySearch(String text) {
        return dataItems.values()
                .stream()
                .filter(item -> item.getAvailable().equals(true) &&
                        item.getDescription().toLowerCase().contains(text.toLowerCase())
                        || item.getName().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }
}