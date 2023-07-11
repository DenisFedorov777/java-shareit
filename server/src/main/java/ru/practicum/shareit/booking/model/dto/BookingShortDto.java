package ru.practicum.shareit.booking.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.statuses.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingShortDto {
    Long id;
    Item item;
    User booker;
    Status status;
}