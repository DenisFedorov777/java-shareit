package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final Map<Long, User> dataUsers = new HashMap<>();

    private volatile Long nextId = 1L;

    private synchronized Long generatedId() {
        return nextId++;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(dataUsers.values());
    }

    @Override
    public User create(User user) {
        user.setId(generatedId());
        dataUsers.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(final Long id) {
        dataUsers.remove(id);
    }

    @Override
    public User update(final User user) {
        dataUsers.put(user.getId(), user);
        return user;
    }
}