package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoWithBooking {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingOwnerDto lastBooking;
    BookingOwnerDto nextBooking;
    List<CommentDto> comments;
}