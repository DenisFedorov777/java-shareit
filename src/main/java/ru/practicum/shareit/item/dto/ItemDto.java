package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class ItemDto {

    Long id;
    @NotBlank(message = "Заполните название")
    String name;
    @NotBlank(message = "Заполните описание")
    String description;
    @NotNull(message = "Не установлен статус")
    Boolean available;
    User owner;
    Long request;
}