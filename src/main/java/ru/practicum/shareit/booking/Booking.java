package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {

    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Item item;
    User booker;
    String statusRate;
}