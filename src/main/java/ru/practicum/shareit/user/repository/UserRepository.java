package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    User create(User user);

    void deleteById(Long id);

    User update(User user, Long id);

    boolean isExistEmail(User user);

    Optional<User> findById(Long id);
}