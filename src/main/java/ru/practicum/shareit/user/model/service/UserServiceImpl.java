package ru.practicum.shareit.user.model.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDto> getUsers() {
        log.debug("Получение списка всех пользователей");
        return repository.findAll().stream()
                .map(user -> UserMapper.toUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        log.debug("Получение пользователя с id = " + id);
        return UserMapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Пользователь с id=%d не найден", id))));
    }

    @Transactional
    @Override
    public UserDto postUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        log.debug("Создание пользователя: " + userDto);
        return UserMapper.toUserDto(repository.save(user));
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto) {
        User existingUser = repository.findById(Long.valueOf(userDto.getId()))
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Пользователь с id=%d не найден", userDto.getId())));
        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }
        User user = repository.save(existingUser);
        log.debug("Обновление пользователя: " + user);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        repository.deleteById(id);
        log.info("Удаление пользователя с id = " + id);
    }
}