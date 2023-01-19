package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.DbGenreStorage;
import ru.yandex.practicum.filmorate.storage.DbMpaStorage;
import ru.yandex.practicum.filmorate.storage.DbUserStorage;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/drop_schema.sql", "/schema.sql", "/test_data.sql"})
class FilmorateApplicationTests {
	private final DbUserStorage userStorage;
	private final DbFilmStorage filmStorage;
	private final DbMpaStorage mpaStorage;
	private final DbGenreStorage genreStorage;

	private User user1 = new User();
	private User user2 = new User();
	private User user3 = new User();

	private Film film1 = new Film();
	private Film film2 = new Film();
	private Film film3 = new Film();

	@Test
	public void testFindUserById() {

		Optional<User> userOptional = Optional.ofNullable(userStorage.findUserById(1));
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1)
				);

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("name", "Ivan")
				);
	}

	@Test
	public void testAddUser() {
		User user = new User();
		user.setName("Василий");
		user.setEmail("vasiliivanych@mail.ru");
		user.setLogin("vanilin");
		user.setBirthday(LocalDate.parse("2007-07-07"));

		Optional<User> userOptional = Optional.ofNullable(userStorage.addUser(user));
		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(userId ->	assertThat(user).hasFieldOrPropertyWithValue("id", 4));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(userLogin ->	assertThat(user).hasFieldOrPropertyWithValue("login", "vanilin"));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(userBirthday -> assertThat(user)
						.hasFieldOrPropertyWithValue("birthday", LocalDate.of(2007, 7, 7)));
	}
	@Test
	public void testAddFilm() {
		Mpa mpa = new Mpa();
		mpa.setId(1);
		Film film = new Film();
		film.setName("Невероятные приключения студентов на практикуме");
		film.setDescription("о-хо-хо-о-ох..");
		film.setReleaseDate(LocalDate.parse("2023-01-18"));
		film.setDuration(1024);
		film.setMpa(mpa);

		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.addFilm(film));
		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(userId ->	assertThat(film).hasFieldOrPropertyWithValue("id", 4));

		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(userBirthday -> assertThat(film)
						.hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2023, 01, 18)));
	}

	@Test
	public void testDeleteFilm() {

		Optional<Integer> filmsOptionalSize = Optional.ofNullable(filmStorage.getFilms().size());
		assertThat(filmsOptionalSize)
				.isPresent()
				.hasValueSatisfying(size ->	AssertionsForClassTypes.assertThat(size).isEqualTo(3));

		filmStorage.deleteFilm(filmStorage.getFilms().get(1));

		filmsOptionalSize = Optional.ofNullable(filmStorage.getFilms().size());
		assertThat(filmsOptionalSize)
				.isPresent()
				.hasValueSatisfying(size ->	AssertionsForClassTypes.assertThat(size).isEqualTo(2));

	}

	@Test
	public void testUpdateFilm() {
		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilms().get(1));
		assertThat(filmOptional.get().getId()).isEqualTo(1);
		assertThat(filmOptional.get().getName()).isEqualTo("Безудержное веселье");

		filmOptional.get().setName("Безвесельное держало");
		filmStorage.updateFilm(filmOptional.get());

		Optional<Film> newFilmOptional = Optional.ofNullable(filmStorage.getFilms().get(1));
		assertThat(newFilmOptional.get().getId()).isEqualTo(1);
		assertThat(newFilmOptional.get().getName()).isEqualTo("Безвесельное держало");
	}

	@Test
	public void testGetFilms() {
		Optional<Integer> filmsOptionalSize = Optional.ofNullable(filmStorage.getFilms().size());
		assertThat(filmsOptionalSize)
				.isPresent()
				.hasValueSatisfying(size ->	AssertionsForClassTypes.assertThat(size).isEqualTo(3));
	}

	@Test
	public void getGenres(){
		Optional<Integer> genresOptionalSize = Optional.ofNullable(genreStorage.getGenres().size());
		assertThat(genresOptionalSize)
				.isPresent()
				.hasValueSatisfying(size ->	AssertionsForClassTypes.assertThat(size).isEqualTo(6));
	}

	@Test
	public void deleteUser() {
		Optional<Integer> usersOptionalSize = Optional.ofNullable(userStorage.getUsers().size());
		assertThat(usersOptionalSize)
				.isPresent()
				.hasValueSatisfying(size ->	AssertionsForClassTypes.assertThat(size).isEqualTo(3));

		userStorage.deleteUser(userStorage.getUsers().get(1));

		usersOptionalSize = Optional.ofNullable(userStorage.getUsers().size());
		assertThat(usersOptionalSize)
				.isPresent()
				.hasValueSatisfying(size ->	AssertionsForClassTypes.assertThat(size).isEqualTo(2));
	}

	@Test
	public void updateUser() {
		Optional<User> userOptional = Optional.ofNullable(userStorage.getUsers().get(1));
		assertThat(userOptional.get().getId()).isEqualTo(1);
		assertThat(userOptional.get().getName()).isEqualTo("Ivan");

		userOptional.get().setName("Stepan");
		userStorage.updateUser(userOptional.get());

		Optional<User> newUserOptional = Optional.ofNullable(userStorage.getUsers().get(1));
		assertThat(newUserOptional.get().getId()).isEqualTo(1);
		assertThat(newUserOptional.get().getName()).isEqualTo("Stepan");
	}

	@Test
	public void getUsers() {
		Optional<Integer> usersOptionalSize = Optional.ofNullable(userStorage.getUsers().size());
		assertThat(usersOptionalSize)
				.isPresent()
				.hasValueSatisfying(size ->	AssertionsForClassTypes.assertThat(size).isEqualTo(3));
	}

}