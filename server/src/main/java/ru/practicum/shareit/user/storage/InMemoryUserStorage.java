package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public User create(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        User oldUser = getById(userId);

        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }

        return oldUser;
    }

    @Override
    public User getById(Long userId) {
        User user = users.get(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    @Override
    public boolean emailExists(String email, Long userId) {
        return users.values()
                .stream()
                .anyMatch(user -> user.getEmail().equals(email)
                        && (userId == null || !user.getId().equals(userId)));
    }
}