package ru.yandex.practicum.filmorate.model

import lombok.Getter
import lombok.Setter
import java.time.LocalDate

class Film {
    var id: Int? = null
    var name: String? = null
    var description: String? = null
    var releaseDate: LocalDate? = null
    var duration: Int? = null
    var likes: List<User> = ArrayList()
    var genre: Set<Genre>? = null
    var rating: RatingMpa? = null

    override fun toString(): String {
        return ("Film{id=" + id
                + ", name=" + name
                + ", release=" + releaseDate
                + ", duration=" + duration + " мин."
                + ", descr.=" + description
                + "}")
    }
}
