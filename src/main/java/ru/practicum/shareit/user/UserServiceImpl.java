package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public User findUserById(Long Id) {
        return repository.findById(Id)
                .orElseThrow(() -> new IllegalArgumentException("Залетный юзер"));
    }

    public UserDto getUserById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Да нет же такого юзера"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        checkExistEmail(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(repository.create(user));
    }

    @Override
    public void deleteUserById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User findUser = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким идентификатором не найден"));
        if (userDto.getEmail() != null && !Objects.equals(userDto.getEmail(), findUser.getEmail())) {
            checkExistEmail(userDto.getEmail());
            findUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            findUser.setName(userDto.getName());
        }
        return UserMapper.toUserDto(repository.update(findUser, userId));
    }

    private void checkExistEmail(String email) {
        if (repository.isExistEmail(email)) {
            throw new ExistEmailException("Такая эл.почта уже есть в системе");
        }
    }
}