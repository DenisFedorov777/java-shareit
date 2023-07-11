package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        return userClient.getUserById(id);
    }

    @PostMapping

    public ResponseEntity<Object> postUser(@Validated(UserDto.NewUser.class) @RequestBody UserDto userDto) {
        return userClient.postUser(userDto);
    }

    @PatchMapping("/{id}")
    @Validated(UserDto.UpdateUser.class)
    public ResponseEntity<Object> updateUser(@Validated(UserDto.UpdateUser.class) @RequestBody UserDto userDto,
                                             @PathVariable Long id) {
        userDto.setId(id);
        return userClient.updateUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        return userClient.deleteUser(id);
    }
}