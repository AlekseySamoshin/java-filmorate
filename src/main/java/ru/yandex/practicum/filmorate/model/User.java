package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class User {
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> friends;

    public User() {
        friends = new HashSet<>();
    }

    @Override
    public String toString() {
        return "User{id=" + id
                + ", login=" + login
                + ", name=" + name
                + ", email=" + email
                + ", birthday=" + birthday
                + "}";
    }
}
