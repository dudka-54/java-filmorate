package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @BeforeEach
    void setUp() {
        // Очистка таблиц перед каждым тестом
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM friendships");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    void testSaveUser() {
        User user = User.builder()
                .email("test@mail.ru")
                .login("testLogin")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User savedUser = userStorage.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@mail.ru");
        assertThat(savedUser.getLogin()).isEqualTo("testLogin");

        User foundUser = userStorage.getUser(savedUser.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void testSaveFilm() {
        Set<Genre> genres = new HashSet<>();
        genres.add(Genre.builder().id(1).name("Комедия").build());

        Film film = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(120)
                .mpa(Mpa.builder().id(1).name("G").build())
                .genres(genres)
                .build();

        Film savedFilm = filmStorage.save(film);

        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm.getId()).isNotNull();
        assertThat(savedFilm.getName()).isEqualTo("Test Film");
        assertThat(savedFilm.getMpa().getId()).isEqualTo(1);
        assertThat(savedFilm.getGenres().size()).isEqualTo(1);

        Film foundFilm = filmStorage.getFilm(savedFilm.getId());

        assertThat(foundFilm).isNotNull();
        assertThat(foundFilm.getId()).isEqualTo(savedFilm.getId());
        assertThat(foundFilm.getGenres().size()).isEqualTo(1);    }
}
