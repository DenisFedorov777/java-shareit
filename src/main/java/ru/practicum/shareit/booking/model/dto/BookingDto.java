package ru.practicum.shareit.booking.model.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    Long itemId;
    @FutureOrPresent
    @NotNull
    LocalDateTime start;
    @FutureOrPresent
    @NotNull
    LocalDateTime end;
}