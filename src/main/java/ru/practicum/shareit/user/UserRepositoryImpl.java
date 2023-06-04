package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

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
    public void deleteById(final Long id) {
        dataUsers.remove(id);
    }

    @Override
    public User update(final User user, Long id) {
        dataUsers.put(id, user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(dataUsers.get(id));
    }

    public boolean isExistEmail(String email) {
        long result = dataUsers.values()
                .stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .count();
        return result > 0;
    }
}