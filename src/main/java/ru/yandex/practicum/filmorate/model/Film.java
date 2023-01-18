package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class Film {
    private java.lang.Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private java.lang.Integer duration;
    private List<java.lang.Integer> likes;
    private Set<Integer> genres;
    private Mpa mpa;

    public Film() {
        likes = new ArrayList<>();
        genres = new HashSet<>();
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
