package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User findUserById(Long userId);
    List<UserDto> getAll();
    UserDto create(UserDto userDto);
    void deleteUserById(Long id);
    UserDto updateUser(UserDto userDto, Long userId);
    UserDto getUserById(Long userId);
}