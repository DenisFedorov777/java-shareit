package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<User> getUsers() {
        return repository.findAll();
    }

    @Override
    public User createUser(User user) {
        return repository.save(user);
    }

    @Override
    public User updateUser(User user, Long userId) {
        return repository.save(user);
    }

    @Override
    public User findUserById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким идентификатором не найден."));
    }

    @Override
    public void deleteUserById(Long userId) {
        repository.delete(repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Ошибка удаления пользователя.")));
    }
}