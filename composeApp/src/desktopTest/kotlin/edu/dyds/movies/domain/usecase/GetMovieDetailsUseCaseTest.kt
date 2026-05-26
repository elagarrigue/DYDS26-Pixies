package edu.dyds.movies.domain.usecase

import edu.dyds.movies.data.fakes.FakeMoviesRepository
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetMovieDetailsUseCaseTest {


    private fun createTestMovie(
        id: Int = 1,
        title: String = "Test Movie",
        overview: String = "A test movie overview",
        releaseDate: String = "2024-01-01",
        poster: String = "/test-poster.jpg",
        backdrop: String? = "/test-backdrop.jpg",
        originalTitle: String = "Original Test Movie",
        originalLanguage: String = "en",
        popularity: Double = 85.5,
        voteAverage: Double = 8.5
    ): Movie = Movie(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        poster = poster,
        backdrop = backdrop,
        originalTitle = originalTitle,
        originalLanguage = originalLanguage,
        popularity = popularity,
        voteAverage = voteAverage
    )

    @Test
    fun `invoke should return movie when movie exists in repository`() = runTest {
        // arrange
        val testMovie = createTestMovie(id = 123, title = "Avatar")
        val repository = FakeMoviesRepository(movies = listOf(testMovie))

        val useCase = GetMovieByTitleUseCaseImpl(repository)

        // act
        val result = useCase(123)

        // assert
        assertEquals(testMovie, result)
        assertEquals(1, repository.getMovieDetailsCallCount)
    }

    @Test
    fun `invoke should return null when movie does not exist in repository`() = runTest {
        // arrange
        val repository = FakeMoviesRepository()
        val useCase = GetMovieByTitleUseCaseImpl(repository)

        // act
        val result = useCase(999)

        // assert
        assertNull(result)
        assertEquals(1, repository.getMovieDetailsCallCount)
    }

    @Test
    fun `invoke should fetch correct movie by id from multiple movies`() = runTest {
        // arrange
        val movie1 = createTestMovie(id = 1, title = "Movie 1")
        val movie2 = createTestMovie(id = 2, title = "Movie 2")
        val movie3 = createTestMovie(id = 3, title = "Movie 3")
        val repository = FakeMoviesRepository(movies = listOf(movie1, movie2, movie3))

        val useCase = GetMovieByTitleUseCaseImpl(repository)

        // act
        val result = useCase(2)

        // assert
        assertEquals(movie2, result)
        assertEquals(movie2.title, result?.title)
        assertEquals(1, repository.getMovieDetailsCallCount)
    }

    @Test
    fun `invoke should return correct movie details with full data`() = runTest {
        // arrange
        val movie = createTestMovie(
            id = 550,
            title = "Fight Club",
            overview = "An insomniac office worker and a devil-may-care soapmaker form an underground fight club...",
            releaseDate = "1999-10-15",
            poster = "/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
            backdrop = "/rr7E0NoGKxvbkb89eR1GwfoYjpA.jpg",
            originalTitle = "Fight Club",
            originalLanguage = "en",
            popularity = 62.5,
            voteAverage = 8.8
        )
        val repository = FakeMoviesRepository(movies = listOf(movie))

        val useCase = GetMovieByTitleUseCaseImpl(repository)

        // act
        val result = useCase(550)

        // assert
        assertEquals(550, result?.id)
        assertEquals("Fight Club", result?.title)
        assertEquals("An insomniac office worker and a devil-may-care soapmaker form an underground fight club...", result?.overview)
        assertEquals("1999-10-15", result?.releaseDate)
        assertEquals(8.8, result?.voteAverage)
        assertEquals("en", result?.originalLanguage)
    }
}

