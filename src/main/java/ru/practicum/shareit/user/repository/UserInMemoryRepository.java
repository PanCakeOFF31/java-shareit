package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@Slf4j
public class UserInMemoryRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>(128);
    private long generatedId = 0;

    @Override
    public long addUser(User user) {
        log.info("UserInMemoryRepository - service.addUser({})", user);

        long id = generateId();

        user.setId(id);
        users.put(id, user);

        return id;
    }

    @Override
    public Optional<User> deleteUserById(long userId) {
        log.info("UserInMemoryRepository - service.deleteUserById({})", userId);

        User deletedUser = users.remove(userId);
        return Optional.ofNullable(deletedUser);
    }

    @Override
    public User updateUser(final User user) {
        log.info("UserInMemoryRepository - service.updateUser({})", user);

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean contains(final long userId) {
        log.info("UserInMemoryRepository - service.contains({})", userId);
        return users.containsKey(userId);
    }

    @Override
    public boolean containsEmail(final String email) {
        log.info("UserInMemoryRepository - service.containsEmail({})", email);

        return users.values().stream()
                .map(User::getEmail)
                .anyMatch(e -> e.equals(email));
    }

    @Override
    public List<User> getAll() {
        log.info("UserInMemoryRepository - service.getAll()");
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findUserById(long userId) {
        log.info("UserInMemoryRepository - service.findUserById({})", userId);

        User user = users.get(userId);

        if (user == null) {
            log.info("Пользователя с id: {} не нашлось в репозитории", userId);
            return Optional.empty();
        }

        log.info("Пользователя с id: {} найден в репозитории", userId);
        return Optional.of(new User(user));
    }

    public int getUserQuantity() {
        log.info("UserInMemoryRepository - service.getUserQuantity()");
        return users.size();
    }

    private long generateId() {
        return ++generatedId;
    }

}
