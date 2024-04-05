package ru.yandex.practicum.filmorate.storage

import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.springframework.stereotype.Component
import ru.yandex.practicum.filmorate.exceptions.WrongDataException
import ru.yandex.practicum.filmorate.model.User

@Component
@Slf4j
class InMemoryUserStorage(
    private val logger: Logger
) : UserStorage {
    private val users = HashMap<Int, User>()
    private var generatedId = 0

    override fun addUser(user: User): User {
        if (user.id == null) user.id = generateId()
        users[user.id!!] = user
        return user
    }

    override fun deleteUser(user: User): User {
        users.remove(user.id)
        return user
    }

    override fun updateUser(user: User): User {
        if (user.id == null || !users.containsKey(user.id)) {
            logger.warn("Ошибка обновления пользователя: неверный id$user")
            throw WrongDataException("Неверный id")
        }
        users[user.id!!] = user
        return user
    }

    override fun getUsers(): HashMap<Int, User> {
        return users
    }

    private fun generateId(): Int {
        return ++generatedId
    }
}
