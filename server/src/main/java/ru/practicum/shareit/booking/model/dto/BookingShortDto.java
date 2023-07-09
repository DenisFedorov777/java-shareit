package ru.practicum.shareit.booking.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.statuses.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingShortDto {
    Long id;
    Item item;
    User booker;
    Status status;
}