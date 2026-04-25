```mermaid
erDiagram
    MPA {
        INTEGER mpa_id PK
        VARCHAR mpa_name
    }

    GENRES {
        INTEGER genre_id PK
        VARCHAR genre_name
    }

    USERS {
        BIGINT id PK
        VARCHAR email
        VARCHAR login
        VARCHAR name
        DATE birthday
    }

    FILMS {
        BIGINT id PK
        VARCHAR name
        TEXT description
        DATE release_date
        INT duration
        INTEGER mpa_id FK
    }

    FILM_GENRES {
        BIGINT film_id FK
        INTEGER genre_id FK
    }

    FILM_LIKES {
        BIGINT film_id FK
        BIGINT user_id FK
    }

    FRIENDSHIPS {
        BIGINT user_id FK
        BIGINT friend_id FK
    }

    MPA ||--o{ FILMS : has_rating
    FILMS ||--o{ FILM_GENRES : has
    GENRES ||--o{ FILM_GENRES : belongs_to
    USERS ||--o{ FILM_LIKES : likes
    FILMS ||--o{ FILM_LIKES : liked_by
    USERS ||--o{ FRIENDSHIPS : adds
    USERS ||--o{ FRIENDSHIPS : friend_of