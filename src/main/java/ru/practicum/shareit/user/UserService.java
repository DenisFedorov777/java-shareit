package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {

    User findUserById(Long userId);

    List<UserDto> getAll();
    UserdTO create(UserDto userDto);
    void deleteUserById(Long id);
    UserDto updateUser(UserDto userDto, Long userId);
}
