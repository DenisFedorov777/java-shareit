package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ExistEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> dataUsers = new HashMap<>();
    private final Map<String, Long> dataEmails = new HashMap<>();
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
        if (!isExistEmail(user)) {
            user.setId(generatedId());
            dataUsers.put(user.getId(), user);
            dataEmails.put(user.getEmail().toLowerCase(), user.getId());
            return user;
        }
        throw new ExistEmailException("Такой email уже зарегистрирован.");
    }

    @Override
    public void deleteById(final Long id) {
        dataEmails.remove(dataUsers.get(id).getEmail());
        dataUsers.remove(id);
    }

    @Override
    public User update(final User user, Long userId) {
        user.setId(userId);
        if (dataUsers.containsKey(userId)) {
            if (isExistEmail(user))
                throw new ExistEmailException("такая почта уже есть");
            User userToUpdate = dataUsers.get(userId);
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                dataEmails.remove(dataUsers.get(userId).getEmail());
                dataEmails.put(user.getEmail().toLowerCase(), userId);
                userToUpdate.setEmail(user.getEmail());
            }
            if (user.getName() != null && !user.getName().isBlank()) {
                userToUpdate.setName(user.getName());
            }
            return userToUpdate;
        }
        throw new NotFoundException("пользователя нет");
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(dataUsers.get(id));
    }

    private boolean isExistEmail(User user) {
        return (dataEmails.containsKey(user.getEmail()) && (!dataEmails.get(user.getEmail()).equals(user.getId())));
    }
}