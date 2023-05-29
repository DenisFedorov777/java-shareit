package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private UserMapper mapper;
    private final UserRepository repository;

    @Override
    public List<UserDto> getAll() {
        return UserMapper.toListUserDto(repository.findAll());
    }

    @Override
    public User create(User user) {
        return repository.create(user);
    }

    @Override
    public void deleteUser(Long id) {
        repository.delete(id);
    }

    @Override
    public User updateUser(User user) {
        return repository.update(user);
    }
}
