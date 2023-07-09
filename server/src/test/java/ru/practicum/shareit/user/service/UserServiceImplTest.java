package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.service.UserServiceImpl;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    User user1;
    User user2;

    @BeforeEach
    public void setUp() {
        user1 = new User(1L, "TestUser", "testuser@test@com");
        user2 = new User(2L, "TesUser2", "testuser2@test.com");
    }

    @Test
    public void getUsersShouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> userList = userService.getUsers();

        assertEquals(userList.size(), 2);
        assertEquals(userList.get(0).getId(), user1.getId());
        assertEquals(userList.get(0).getEmail(), user1.getEmail());
        assertEquals(userList.get(1).getId(), user2.getId());
        assertEquals(userList.get(1).getName(), user2.getName());
    }

    @Test
    public void getUserByIdShouldReturnUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user2));

        UserDto resultUser = userService.getUserById(2L);

        assertEquals(resultUser.getId(), user2.getId());
        assertEquals(resultUser.getName(), user2.getName());
        assertEquals(resultUser.getEmail(), user2.getEmail());
    }

    @Test
    public void getUserByIdShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(user1.getId()));
    }

    @Test
    public void postUserShouldCreate() {
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserDto resultUser = userService.postUser(UserMapper.toUserDto(user1));

        assertEquals(resultUser.getId(), user1.getId());
        assertEquals(resultUser.getName(), user1.getName());
        assertEquals(resultUser.getEmail(), user1.getEmail());
    }

    @Test
    public void updateUserShouldUpdate() {
        when(userRepository.save(any(User.class))).thenReturn(user1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));

        UserDto resultUser = userService.updateUser(UserMapper.toUserDto(user1));

        assertEquals(UserMapper.toUserDto(user1), resultUser);
        assertEquals(user1.getName(), resultUser.getName());
        assertEquals(user1.getEmail(), resultUser.getEmail());
    }

    @Test
    public void updateUserShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(UserMapper.toUserDto(user1)));
    }

    @Test
    public void deleteUserShouldDeleteUserById() {
        userService.deleteUser(user1.getId());

        verify(userRepository).deleteById(user1.getId());
    }
}