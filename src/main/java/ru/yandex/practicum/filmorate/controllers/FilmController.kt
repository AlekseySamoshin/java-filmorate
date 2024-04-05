package ru.yandex.practicum.filmorate.controllers

import lombok.extern.slf4j.Slf4j
import org.apache.logging.slf4j.SLF4JLogger
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ru.yandex.practicum.filmorate.model.Film
import ru.yandex.practicum.filmorate.services.FilmService

@RestController
@Slf4j
class FilmController @Autowired constructor(
    private val filmService: FilmService,
    private val logger: Logger
) {
    @get:GetMapping("/films")
    val films: Collection<Film>
        get() {
            logger.info("Получен запрос на получение списка фильмов")
            return filmService.films.values
        }

    @GetMapping("/films/{id}")
    fun getFilm(@PathVariable id: Int): Film {
        logger.info("Получен запрос на получение фильма")
        return filmService.getFilmById(id)
    }

    @GetMapping("/films/popular")
    fun getMostPopularFilms(@RequestParam(defaultValue = "0") count: Int): List<Film> {
        return filmService.getMostPopular(count)
    }

    @PostMapping("/films")
    fun addFilm(@RequestBody film: Film): Film {
        var addedFilm = film
        logger.info("Получен запрос на добавление фильма.")
        addedFilm = filmService.addFilm(addedFilm)
        logger.info("Добавлен фильм $addedFilm")
        return addedFilm
    }

    @PutMapping("/films")
    fun updateFilm(@RequestBody film: Film?): Film? {
        var addedFilm = film
        logger.info("Получен запрос на обновление данных фильма.")
        addedFilm = filmService.updateFilm(addedFilm)
        return addedFilm
    }

    @PutMapping("/films/{id}/like/{userId}")
    fun addLike(@PathVariable id: Int, @PathVariable userId: Int) {
        filmService.addLike(id, userId)
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    fun removeLike(@PathVariable id: Int, @PathVariable userId: Int) {
        filmService.removeLike(id, userId)
    }
}
