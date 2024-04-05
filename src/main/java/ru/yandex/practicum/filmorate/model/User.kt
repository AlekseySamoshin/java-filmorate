package ru.yandex.practicum.filmorate.model

import lombok.Getter
import lombok.Setter
import java.time.LocalDate

class User {
    var id: Int? = null
    var email: String? = null
    var login: String? = null
    var name: String? = null
    var birthday: LocalDate? = null
    var friends: Set<Int> = HashSet()

    override fun toString(): String {
        return ("User{id=" + id
                + ", login=" + login
                + ", name=" + name
                + ", email=" + email
                + ", birthday=" + birthday
                + "}")
    }
}