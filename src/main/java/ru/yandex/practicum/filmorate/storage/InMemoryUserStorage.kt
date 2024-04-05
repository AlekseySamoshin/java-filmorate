package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();
    private int generatedId;

    @Override
    public User addUser(User user) {
        if (user.getId() == null) user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User deleteUser(User user) {
        users.remove(user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.warn("Ошибка обновления пользователя: неверный id" + user.toString());
            throw new WrongDataException("Неверный id");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public HashMap<Integer, User> getUsers() {
        return users;
    }

    private int generateId() {
        return ++generatedId;
    }

}
