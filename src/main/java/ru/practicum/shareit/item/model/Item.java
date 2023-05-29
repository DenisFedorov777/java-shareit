package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {

    private Long id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private String request;
}