package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT i FROM Item i " +
            "WHERE (LOWER(i.name) LIKE CONCAT('%', LOWER(:text), '%') " +
            "OR LOWER(i.description) LIKE CONCAT('%', LOWER(:text), '%')) " +
            "AND i.available = true")
    Page<Item> searchByText(@Param("text") String text, Pageable pageable);
}