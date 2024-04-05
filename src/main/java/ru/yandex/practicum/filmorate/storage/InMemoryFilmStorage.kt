package ru.yandex.practicum.filmorate.storage

import lombok.extern.slf4j.Slf4j
import org.apache.logging.slf4j.SLF4JLogger
import org.slf4j.Logger
import org.springframework.stereotype.Component
import ru.yandex.practicum.filmorate.exceptions.WrongDataException
import ru.yandex.practicum.filmorate.model.Film

@Component
@Slf4j
class InMemoryFilmStorage(
    private val logger: Logger
) : FilmStorage {
    private val films = HashMap<Int, Film>()
    private var generatedId = 0

    override fun addFilm(film: Film): Film {
        if (film.id == null) film.id = generateId()
        films[film.id!!] = film
        return film
    }

    override fun deleteFilm(film: Film): Film {
        films.remove(film.id)
        return film
    }

    override fun updateFilm(film: Film): Film {
        if (film.id == null || !films.containsKey(film.id)) {
            logger.warn("Ошибка обновления данных фильма: неверный id$film")
            throw WrongDataException("Неверный id")
        }
        films[film.id!!] = film
        logger.info("Обновлены данные фильма $film")
        return film
    }

    override fun getFilms(): HashMap<Int, Film> {
        return films
    }

    private fun generateId(): Int {
        return ++generatedId
    }
}
