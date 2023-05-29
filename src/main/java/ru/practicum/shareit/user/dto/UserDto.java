package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
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
