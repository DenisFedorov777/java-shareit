package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private volatile Long id = 1L;
    private final Map<Long, Item> dataItems = new HashMap<>();

    private synchronized Long generatedId() {
        return id++;
    }

    @Override
    public Item addItem(Item item) {
        item.setId(generatedId());
        dataItems.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(dataItems.get(id));
    }

    @Override
    public void updateById(Item item, Long id) {
        dataItems.put(id, item);
    }

    @Override
    public List<Item> findByUserId(Long userId) {
        return dataItems.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
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