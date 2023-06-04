package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class UserDto {

    @Positive
    Long id;

    @NotBlank(message = "Ошибка - укажите имя")
    String name;

    @Email(message = "Ошибка - некорректный формат электронной почты")
    @NotBlank(message = "Ошибка - укажите электронную почту")
    String email;
}