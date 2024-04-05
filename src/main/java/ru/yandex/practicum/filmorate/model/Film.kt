package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private List<User> likes;
    private Set<Genre> genre;
    private RatingMpa rating;

    public Film() {
        likes = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Film{id=" + id
                + ", name=" + name
                + ", release=" +  releaseDate
                + ", duration=" + duration + " мин."
                + ", descr.=" + description
                + "}";
    }
}
