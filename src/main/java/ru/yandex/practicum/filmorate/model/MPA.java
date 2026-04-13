package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;

@RequiredArgsConstructor
public enum MPA {
    G("G"),
    PG("PG"),
    PG_13("PG-13"),
    R("R"),
    NC_17("NC-17");

    private final String mpa;

    @Override
    public String toString() {
        return mpa;
    }

    @JsonValue
    public String getMpa() {
        return mpa;
    }

    @JsonCreator
    public static MPA jsonName(String name) {
        for (MPA rating : values()) {
            if (rating.mpa.equalsIgnoreCase(name)) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Неизвестный рейтинг MPA: '" + name +
                "'. Допустимые значения: " + Arrays.toString(values()));
    }
}
