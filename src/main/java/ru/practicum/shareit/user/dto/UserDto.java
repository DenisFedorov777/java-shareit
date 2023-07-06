package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    @Email(groups = {UserDto.OnCreate.class}, message = "must be a well-formed email address")
    @NotBlank(groups = {UserDto.OnCreate.class}, message = "must not be blank")
    private String email;

    public interface OnCreate {
    }
}