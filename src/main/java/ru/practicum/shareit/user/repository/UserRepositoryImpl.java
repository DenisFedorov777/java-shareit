package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> dataUsers = new HashMap<>();
    private final Set<String> dataEmails = new HashSet<>();
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
        dataEmails.add(user.getEmail());
        return user;
    }

    @Override
    public void deleteById(final Long id) {
        dataEmails.remove(dataUsers.get(id).getEmail());
        dataUsers.remove(id);
    }

    @Override
    public User update(final User user, Long userId) {
        if(dataEmails.contains(dataUsers.get(userId).getEmail())) {
            dataEmails.remove(user.getEmail());
        }
        dataEmails.add(user.getEmail());
        dataUsers.put(userId, user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(dataUsers.get(id));
    }

    public boolean isExistEmail(String email) {
        //return dataEmails.contains(email);
        long result = dataUsers.values()
                .stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .count();
        return result > 0;
    }
}