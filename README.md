# DYDS - Proyecto

Arquitectura aplicada en `desktopMain`:

- `data`: acceso a datos remotos (TMDB), mapeos y cache en memoria.
- `domain`: entidades, contrato de repositorio y casos de uso.
- `presentation`: UI Compose, navegacion y `ViewModel` orientado a `UiState`.
- `di`: inyector manual de dependencias.

Estructura principal:

```text
desktopMain
└── kotlin
    └── edu.dyds.movies
        ├── data
        │   ├── external
        │   ├── local
        │   └── MoviesRepositoryImpl.kt
        ├── di
        │   └── MoviesDependencyInjector.kt
        ├── domain
        │   ├── entity
        │   │   ├── Movie.kt
        │   │   └── QualifiedMovie.kt
        │   ├── repository
        │   │   └── MoviesRepository.kt
        │   └── usecase
        │       ├── GetMovieDetailsUseCase.kt
        │       └── GetPopularMoviesUseCase.kt
        ├── presentation
        │   ├── detail
        │   ├── home
        │   ├── utils
        │   ├── App.kt
        │   └── Navigation.kt
        └── main.kt
```