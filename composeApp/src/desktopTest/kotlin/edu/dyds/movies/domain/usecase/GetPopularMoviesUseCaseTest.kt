package edu.dyds.movies.domain.usecase

import edu.dyds.movies.data.fakes.FakeMoviesRepository
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetPopularMoviesUseCaseTest {

    private fun movie(id: Int, voteAverage: Double, title: String = "Movie $id"): Movie {
        return Movie(
            id = id,
            title = title,
            overview = "Overview $id",
            releaseDate = "2025-01-01",
            poster = "poster_$id.jpg",
            backdrop = null,
            originalTitle = title,
            originalLanguage = "en",
            popularity = 100.0 + id,
            voteAverage = voteAverage
        )
    }

    @Test
    fun `invoke orders movies by vote descending`() = runTest {
        // arrange
        val fakeRepository = FakeMoviesRepository(
            movies = listOf(
                movie(id = 1, voteAverage = 5.5),
                movie(id = 2, voteAverage = 9.0),
                movie(id = 3, voteAverage = 6.2)
            )
        )
        val useCase = GetPopularMoviesUseCaseImpl(fakeRepository)

        // act
        val result = useCase()

        // assert
        assertEquals(listOf(2, 3, 1), result.map { it.movie.id })
        assertEquals(1, fakeRepository.getPopularMoviesCalls)
    }

    @Test
    fun `invoke marks movie with vote 6 as good`() = runTest {
        // arrange
        val fakeRepository = FakeMoviesRepository(
            movies = listOf(movie(id = 10, voteAverage = 6.0))
        )
        val useCase = GetPopularMoviesUseCaseImpl(fakeRepository)

        // act
        val result = useCase()

        // assert
        assertEquals(1, result.size)
        assertTrue(result.first().isGoodMovie)
    }

    @Test
    fun `invoke marks movie below 6 as not good`() = runTest {
        // arrange
        val fakeRepository = FakeMoviesRepository(
            movies = listOf(movie(id = 11, voteAverage = 5.9))
        )
        val useCase = GetPopularMoviesUseCaseImpl(fakeRepository)

        // act
        val result = useCase()

        // assert
        assertEquals(1, result.size)
        assertFalse(result.first().isGoodMovie)
    }

    @Test
    fun `invoke returns empty list when repository has no movies`() = runTest {
        // arrange
        val fakeRepository = FakeMoviesRepository()
        val useCase = GetPopularMoviesUseCaseImpl(fakeRepository)

        // act
        val result = useCase()

        // assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke preserves movie data inside qualified movie`() = runTest {
        // arrange
        val original = movie(id = 42, voteAverage = 7.7, title = "Original Title").copy(
            overview = "Specific overview",
            releaseDate = "2024-10-31",
            poster = "poster_42_custom.jpg",
            backdrop = "backdrop_42_custom.jpg",
            originalLanguage = "es",
            popularity = 999.9
        )
        val fakeRepository = FakeMoviesRepository(movies = listOf(original))
        val useCase = GetPopularMoviesUseCaseImpl(fakeRepository)

        // act
        val result = useCase().single()

        // assert
        assertEquals(original, result.movie)
        assertTrue(result.isGoodMovie)
    }
}
