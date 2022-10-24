package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        userStorage.addUser(user);
        return user;
    }

    public User updateUser(User user) {
        if (!getUsers().containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден.");
        }
        userStorage.updateUser(user);
        return user;
    }

    public Map<Integer, User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(int id) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return userStorage.getUsers().get(id);
    }

    public void addFriend(int id, int friendId) {
        if (!getUsers().containsKey(id) || !getUsers().containsKey(friendId)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        //if (id == friendId) throw new WrongDataException("Нельзя добавить в друзья самого себя :(");
        getUserById(id).getFriends().add(getUserById(friendId).getId());
        getUserById(friendId).getFriends().add(getUserById(id).getId());
    }

    public void deleteFriend(int id, int friendId) {
        if (!getUsers().containsKey(id) || !getUsers().containsKey(friendId)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        if(!getFriends(id).contains(getUserById(friendId))) {
            throw new NotFoundException("Пользователь id=" + friendId + " не найден среди друзей пользователя id=" + id);
        }
        getUserById(id).getFriends().remove(getUserById(friendId));
        getUserById(friendId).getFriends().remove(getUserById(id));
    }

    public List<User> getFriends(int id) {
        if (!userStorage.getUsers().containsKey(id) || userStorage.getUsers().isEmpty()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return getUserById(id).getFriends().stream()
                .map((userId) -> getUserById(userId))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int otherId) {
        if (!getUsers().containsKey(id) || !getUsers().containsKey(otherId)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return getFriends(id).stream()
                .filter(getFriends(otherId) :: contains)
                .collect(Collectors.toList());
    }
}
