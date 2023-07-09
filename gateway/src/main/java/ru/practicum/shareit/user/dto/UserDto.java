package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/*
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    @Email(groups = {OnCreate.class}, message = "must be a well-formed email address")
    @NotBlank(groups = {OnCreate.class}, message = "must not be blank")
    private String email;

    public interface OnCreate {
    }
}*/

@Data
@Builder
public class UserDto {
    private long id;
    @NotBlank(groups = {NewUser.class})
    private String name;
    @Email(groups = {NewUser.class, UpdateUser.class})
    @NotBlank(groups = {NewUser.class})
    private String email;

    public interface NewUser {
    }

    public interface UpdateUser {
    }
}