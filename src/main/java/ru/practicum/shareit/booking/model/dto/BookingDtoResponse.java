package ru.practicum.shareit.booking.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.statuses.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.BookerDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDtoResponse {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    ItemDto item;
    BookerDto booker;
    BookingStatus status;
}