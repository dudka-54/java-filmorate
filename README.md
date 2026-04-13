```mermaid
erDiagram
    USERS {
        BIGINT id PK
        VARCHAR email
        VARCHAR login
        VARCHAR name
        DATE birthday
        VARCHAR friendship_status
    }

    FILMS {
        BIGINT id PK
        VARCHAR name
        TEXT description
        DATE release_date
        INT duration
        VARCHAR genre
        VARCHAR mpa
    }

    FILM_LIKES {
        BIGINT film_id FK
        BIGINT user_id FK
    }

    USER_FRIENDS {
        BIGINT user_id FK
        BIGINT friend_id FK
    }

    USERS ||--o{ FILM_LIKES : likes
    FILMS ||--o{ FILM_LIKES : liked_by

    USERS ||--o{ USER_FRIENDS : has
    USERS ||--o{ USER_FRIENDS : friend