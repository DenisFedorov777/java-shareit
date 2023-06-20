package ru.practicum.shareit.user.mapper;

import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Generated
public class UserPatchMapper {

    private final UserRepository repository;

    public User toUser(UserPatchDto userPatchDto, Long userId) {
        userPatchDto.setId(userId);
        Optional<User> userOptional = repository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not exists");
        }
        User user = userOptional.get();
        if (userPatchDto.getName() == null) {
            userPatchDto.setName(user.getName());
        }
        if (userPatchDto.getEmail() == null) {
            userPatchDto.setEmail(user.getEmail());
        }
        return User.builder()
                .id(userPatchDto.getId())
                .name(userPatchDto.getName())
                .email(userPatchDto.getEmail())
                .build();
    }
}