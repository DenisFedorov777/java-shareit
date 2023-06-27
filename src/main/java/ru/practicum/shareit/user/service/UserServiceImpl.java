package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<User> getUsers() {
        return repository.findAll();
    }

    @Transactional
    @Override
    public User createUser(User user) {
        return repository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(User user, Long userId) {
        User updateUser = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким идентификатором не найден."));
        Optional.ofNullable(user.getName()).ifPresent(updateUser::setName);
        Optional.ofNullable(user.getEmail()).ifPresent(updateUser::setEmail);

        return repository.save(updateUser);
    }

    @Override
    public User findUserById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким идентификатором не найден."));
    }

    @Transactional
    @Override
    public void deleteUserById(Long userId) {
        repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким идентификатором не найден."));
        repository.deleteById(userId);
    }
}