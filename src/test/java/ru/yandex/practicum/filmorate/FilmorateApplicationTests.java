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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userStorage;
    private FilmDbStorage filmStorage;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM film_genres");
        jdbcTemplate.update("DELETE FROM friendships");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");

        userStorage = new UserDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate);
    }

    private User createTestUser() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 5, 10));
        return user;
    }

    @Test
    void testSaveUser() {
        User user = createTestUser();
        User saved = userStorage.save(user);

        assertNotNull(saved.getId());
        assertEquals("test@mail.ru", saved.getEmail());
        assertEquals("testLogin", saved.getLogin());
        assertNotNull(saved.getFriends());
    }

    @Test
    void testGetUser() {
        User saved = userStorage.save(createTestUser());
        User found = userStorage.getUser(saved.getId()).get();

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("test@mail.ru", found.getEmail());
        assertNotNull(found.getFriends());
    }

    @Test
    void testGetUserNotFound() {
        try {
            User found = userStorage.getUser(9999L).get();
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
        }
    }

    @Test
    void testUpdateUser() {
        User saved = userStorage.save(createTestUser());
        saved.setName("Updated Name");
        saved.setEmail("updated@mail.ru");

        User updated = userStorage.update(saved);
        assertEquals("Updated Name", updated.getName());

        User found = userStorage.getUser(saved.getId()).get();
        assertEquals("Updated Name", found.getName());
        assertEquals("updated@mail.ru", found.getEmail());
    }

    @Test
    void testFindAllUsers() {
        User user1 = createTestUser();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("login1");
        userStorage.save(user1);

        User user2 = createTestUser();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("login2");
        userStorage.save(user2);

        Collection<User> users = userStorage.findAll();
        assertEquals(2, users.size());
    }

    @Test
    void testAddFriend() {
        User user1 = userStorage.save(createTestUser());
        User user2 = userStorage.save(createTestUser());

        userStorage.addFriend(user1.getId(), user2.getId());

        User updated = userStorage.getUser(user1.getId()).get();
        assertTrue(updated.getFriends().contains(user2.getId()));
        assertEquals(1, updated.getFriends().size());
    }

    @Test
    void testDeleteFriend() {
        User user1 = userStorage.save(createTestUser());
        User user2 = userStorage.save(createTestUser());

        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.deleteFriend(user1.getId(), user2.getId());

        User updated = userStorage.getUser(user1.getId()).get();
        assertEquals(0, updated.getFriends().size());
    }

    @Test
    void testAddDuplicateFriend() {
        User user1 = userStorage.save(createTestUser());
        User user2 = userStorage.save(createTestUser());

        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.addFriend(user2.getId(), user1.getId());

        User updated = userStorage.getUser(user1.getId()).get();
        assertEquals(1, updated.getFriends().size());
    }

    private Film createTestFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 3, 15));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film.setMpa(mpa);

        film.setGenres(new ArrayList<>());
        film.setLikes(new HashSet<>());
        return film;
    }

    @Test
    void testSaveFilm() {
        Film film = createTestFilm();
        Film saved = filmStorage.save(film);

        assertNotNull(saved.getId());
        assertEquals("Test Film", saved.getName());
        assertEquals(1, saved.getMpa().getId());
    }

    @Test
    void testGetFilm() {
        Film saved = filmStorage.save(createTestFilm());
        Film found = filmStorage.getFilm(saved.getId()).get();

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Test Film", found.getName());
        assertNotNull(found.getMpa());
        assertEquals("G", found.getMpa().getName());
        assertNotNull(found.getGenres());
        assertNotNull(found.getLikes());
    }

    @Test
    void testGetFilmNotFound() {
        try {
            filmStorage.getFilm(9999L);
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
        }
    }

    @Test
    void testUpdateFilm() {
        Film saved = filmStorage.save(createTestFilm());
        saved.setName("Updated Film");
        saved.setDescription("Updated Description");

        Film updated = filmStorage.update(saved);
        assertEquals("Updated Film", updated.getName());

        Film found = filmStorage.getFilm(saved.getId()).get();
        assertEquals("Updated Film", found.getName());
        assertEquals("Updated Description", found.getDescription());
    }

    @Test
    void testGetAllFilms() {
        filmStorage.save(createTestFilm());
        filmStorage.save(createTestFilm());

        Map<Long, Film> films = filmStorage.getFilms();
        assertEquals(2, films.size());
    }

    @Test
    void testSaveFilmWithGenres() {
        Film film = createTestFilm();
        List<Genre> genres = new ArrayList<>();
        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");
        genres.add(genre1);
        film.setGenres(genres);

        Film saved = filmStorage.save(film);
        Film found = filmStorage.getFilm(saved.getId()).get();

        assertEquals(1, found.getGenres().size());
    }

    @Test
    void testAddLike() {
        Film film = filmStorage.save(createTestFilm());
        User user = userStorage.save(createTestUser());

        filmStorage.addLike(film.getId(), user.getId());

        Film found = filmStorage.getFilm(film.getId()).get();
        assertEquals(1, found.getLikes().size());
    }

    @Test
    void testDeleteLike() {
        Film film = filmStorage.save(createTestFilm());
        User user = userStorage.save(createTestUser());

        filmStorage.addLike(film.getId(), user.getId());
        filmStorage.deleteLike(film.getId(), user.getId());

        Film found = filmStorage.getFilm(film.getId()).get();
        assertEquals(0, found.getLikes().size());
    }
}