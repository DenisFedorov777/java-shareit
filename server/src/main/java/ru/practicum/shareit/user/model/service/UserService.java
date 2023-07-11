package ru.practicum.shareit.user.model.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    UserDto getUserById(Long id);

    UserDto postUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    void deleteUser(Long id);
}