package edu.dyds.movies.data.external

import edu.dyds.movies.data.fakes.FakeMovieDetailRemoteSource
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MovieBrokerTest {

    private fun createMovie(
        id: Int = 1,
        title: String = "Avatar",
        overview: String = "",
        popularity: Double = 0.0,
        voteAverage: Double = 0.0
    ): Movie = Movie(
        id = id,
        title = title,
        overview = overview,
        releaseDate = "2009-12-15",
        poster = "/poster.jpg",
        backdrop = "/backdrop.jpg",
        originalTitle = title,
        originalLanguage = "en",
        popularity = popularity,
        voteAverage = voteAverage
    )

    @Test
    fun `getMovieByTitle returns combined movie when both sources have data`() = runTest {
        // arrange
        val tmdbMovie = createMovie(id = 10, title = "Avatar", overview = "Gente azul", popularity = 80.0, voteAverage = 8.0)
        val omdbMovie = createMovie(id = 99, title = "Avatar", overview = "Pandora", popularity = 100.0, voteAverage = 9.0)
        
        val tmdbSource = FakeMovieDetailRemoteSource(tmdbMovie)
        val omdbSource = FakeMovieDetailRemoteSource(omdbMovie)
        val broker = MovieBroker(tmdbSource, omdbSource)

        // act
        val result = broker.getMovieByTitle("Avatar")

        // assert
        assertEquals("TMDB: Gente azul\n\nOMDB: Pandora", result?.overview)
        assertEquals(90.0, result?.popularity)
        assertEquals(8.5, result?.voteAverage)
        assertEquals(10, result?.id)
    }

    @Test
    fun `getMovieByTitle returns TMDB movie with prefix when OMDB is missing`() = runTest {
        // arrange
        val tmdbMovie = createMovie(title = "Avatar", overview = "Gente azul")
        
        val tmdbSource = FakeMovieDetailRemoteSource(tmdbMovie)
        val omdbSource = FakeMovieDetailRemoteSource(null)
        val broker = MovieBroker(tmdbSource, omdbSource)

        // act
        val result = broker.getMovieByTitle("Avatar")

        // assert
        assertEquals("TMDB: Gente azul", result?.overview)
    }

    @Test
    fun `getMovieByTitle returns OMDB movie with prefix when TMDB is missing`() = runTest {
        // arrange
        val omdbMovie = createMovie(title = "Avatar", overview = "Pandora")
        
        val tmdbSource = FakeMovieDetailRemoteSource(null)
        val omdbSource = FakeMovieDetailRemoteSource(omdbMovie)
        val broker = MovieBroker(tmdbSource, omdbSource)

        // act
        val result = broker.getMovieByTitle("Avatar")

        // assert
        assertEquals("OMDB: Pandora", result?.overview)
    }

    @Test
    fun `getMovieByTitle returns null when both sources return null`() = runTest {
        // arrange
        val tmdbSource = FakeMovieDetailRemoteSource(null)
        val omdbSource = FakeMovieDetailRemoteSource(null)
        val broker = MovieBroker(tmdbSource, omdbSource)

        // act
        val result = broker.getMovieByTitle("Avatar")

        // assert
        assertNull(result)
    }

    @Test
    fun `buildMovie handles empty overview from one source correctly`() = runTest {
        // arrange
        val tmdbMovie = createMovie(title = "Avatar", overview = "")
        val omdbMovie = createMovie(title = "Avatar", overview = "Pandora")
        
        val tmdbSource = FakeMovieDetailRemoteSource(tmdbMovie)
        val omdbSource = FakeMovieDetailRemoteSource(omdbMovie)
        val broker = MovieBroker(tmdbSource, omdbSource)

        // act
        val result = broker.getMovieByTitle("Avatar")

        // assert
        assertEquals("OMDB: Pandora", result?.overview)
    }
}