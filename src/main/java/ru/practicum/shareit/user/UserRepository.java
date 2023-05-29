package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserRepository {

    List<User> findAll();
    User create(User user);
    void delete(Long id);
    User update(User user);
}
