package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserPatchMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;
    private final UserMapper userMapper;
    private final UserPatchMapper patchMapper;

    @GetMapping
    public List<UserDto> getUsers() {
        return service.getUsers()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(service.createUser(user));
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Valid @RequestBody UserPatchDto userPatchDto, @PathVariable("id") Long userId) {
        User user = patchMapper.toUser(userPatchDto, userId);
        return userMapper.toUserDto(service.updateUser(user, userId));
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable("id") Long userId) {
        return userMapper.toUserDto(service.findUserById(userId));
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") Long userId) {
        service.deleteUserById(userId);
    }
}