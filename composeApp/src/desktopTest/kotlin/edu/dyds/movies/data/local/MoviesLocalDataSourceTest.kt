package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MoviesLocalDataSourceTest {

    private val dataSource = MoviesLocalDataSourceImpl()

    @Test
    fun `getPopularMovies returns empty list initially`() {
        // act
        val result = dataSource.getPopularMovies()

        // assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `savePopularMovies saves correctly and getPopularMovies returns saved movies`() {
        // arrange
        val movies = listOf(
            Movie(
                id = 1,
                title = "Movie 1",
                overview = "Overview 1",
                releaseDate = "2023-01-01",
                poster = "poster1.jpg",
                backdrop = "backdrop1.jpg",
                originalTitle = "Original Movie 1",
                originalLanguage = "en",
                popularity = 10.0,
                voteAverage = 8.0
            ),
            Movie(
                id = 2,
                title = "Movie 2",
                overview = "Overview 2",
                releaseDate = "2023-01-02",
                poster = "poster2.jpg",
                backdrop = "backdrop2.jpg",
                originalTitle = "Original Movie 2",
                originalLanguage = "en",
                popularity = 9.0,
                voteAverage = 7.0
            )
        )

        // act
        dataSource.savePopularMovies(movies)
        val result = dataSource.getPopularMovies()

        // assert
        assertEquals(2, result.size)
        assertEquals(movies, result)
    }

    @Test
    fun `savePopularMovies replaces previous movies`() {
        // arrange
        val initialMovies = listOf(
            Movie(
                id = 1,
                title = "Movie 1",
                overview = "Overview 1",
                releaseDate = "2023-01-01",
                poster = "poster1.jpg",
                backdrop = "backdrop1.jpg",
                originalTitle = "Original Movie 1",
                originalLanguage = "en",
                popularity = 10.0,
                voteAverage = 8.0
            )
        )
        val newMovies = listOf(
            Movie(
                id = 3,
                title = "Movie 3",
                overview = "Overview 3",
                releaseDate = "2023-01-03",
                poster = "poster3.jpg",
                backdrop = "backdrop3.jpg",
                originalTitle = "Original Movie 3",
                originalLanguage = "en",
                popularity = 11.0,
                voteAverage = 9.0
            )
        )

        // act
        dataSource.savePopularMovies(initialMovies)
        dataSource.savePopularMovies(newMovies)
        val result = dataSource.getPopularMovies()

        // assert
        assertEquals(1, result.size)
        assertEquals(newMovies, result)
    }
}
