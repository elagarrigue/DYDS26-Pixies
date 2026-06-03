package edu.dyds.movies.domain.repository

import edu.dyds.movies.data.MoviesRepositoryImpl
import edu.dyds.movies.data.fakes.FakeMovieDetailRemoteSource
import edu.dyds.movies.data.fakes.FakeMoviesLocalDataSource
import edu.dyds.movies.data.fakes.FakeMoviesRemoteDataSource
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue


class MoviesRepositoryImplTest {

    private fun createMovie(id: Int = 1, title: String = "Test Movie $id"): Movie {
        return Movie(
            id = id,
            title = title,
            overview = "Test Overview",
            releaseDate = "2024-01-01",
            poster = "https://image.tmdb.org/t/p/w185/test$id.jpg",
            backdrop = "https://image.tmdb.org/t/p/w780/backdrop$id.jpg",
            originalTitle = "Original Title $id",
            originalLanguage = "en",
            popularity = 7.5,
            voteAverage = 8.0
        )
    }

    @Test
    fun `getPopularMovies should return cached movies when cache is not empty`() = runTest {
        // arrange
        val cached = listOf(
            createMovie(
                id = 1,
                title = "Cached Movie"
            )
        )
        val localDataSource = FakeMoviesLocalDataSource().apply {
            savePopularMovies(cached)
        }
        val popularMoviesRemoteDataSource = FakeMoviesRemoteDataSource(Result.success(emptyList()))
        val movieDetailRemoteSource = FakeMovieDetailRemoteSource() // Added
        val repository = MoviesRepositoryImpl(movieDetailRemoteSource, popularMoviesRemoteDataSource, localDataSource) // Updated

        // act
        val result = repository.getPopularMovies()

        // assert
        assertEquals(cached, result)
    }

    @Test
    fun `getPopularMovies should fetch from remote when cache is empty`() = runTest {
        // arrange
        val remoteMovies = listOf(
            createMovie(1),
            createMovie(2)
        )
        val localDataSource = FakeMoviesLocalDataSource()
        val popularMoviesRemoteDataSource = FakeMoviesRemoteDataSource(Result.success(remoteMovies))
        val movieDetailRemoteSource = FakeMovieDetailRemoteSource() // Added
        val repository = MoviesRepositoryImpl(movieDetailRemoteSource, popularMoviesRemoteDataSource, localDataSource) // Updated

        // act
        val result = repository.getPopularMovies()

        // assert
        assertTrue(result.isNotEmpty())
        assertEquals(2, result.size)
        assertEquals("Test Movie 1", result[0].title)
        assertEquals("Test Movie 2", result[1].title)
    }

    @Test
    fun `getPopularMovies should save to cache after fetching from remote`() = runTest {
        // arrange
        val remoteMovies = listOf(createMovie(1))
        val localDataSource = FakeMoviesLocalDataSource()
        val popularMoviesRemoteDataSource = FakeMoviesRemoteDataSource(Result.success(remoteMovies))
        val movieDetailRemoteSource = FakeMovieDetailRemoteSource() // Added
        val repository = MoviesRepositoryImpl(movieDetailRemoteSource, popularMoviesRemoteDataSource, localDataSource) // Updated

        // act
        repository.getPopularMovies()

        // assert
        val cachedMovies = localDataSource.getPopularMovies()
        assertEquals(1, cachedMovies.size)
        assertEquals("Test Movie 1", cachedMovies[0].title)
    }

    @Test
    fun `getPopularMovies should return empty list when remote throws exception`() = runTest {
        // arrange
        val localDataSource = FakeMoviesLocalDataSource()
        val popularMoviesRemoteDataSource = FakeMoviesRemoteDataSource(
            Result.failure(Exception("Network error"))
        )
        val movieDetailRemoteSource = FakeMovieDetailRemoteSource() // Added
        val repository = MoviesRepositoryImpl(movieDetailRemoteSource, popularMoviesRemoteDataSource, localDataSource) // Updated

        // act
        val result = repository.getPopularMovies()

        // assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getMovieByTitle should return movie when successful`() = runTest {
        // arrange
        val movie = createMovie(42, "Fight Club")
        val localDataSource = FakeMoviesLocalDataSource()
        val popularMoviesRemoteDataSource = FakeMoviesRemoteDataSource(Result.success(emptyList())) // Not used in this test, but required by constructor
        val movieDetailRemoteSource = FakeMovieDetailRemoteSource(movieToReturn = movie) // Configured for this test
        val repository = MoviesRepositoryImpl(movieDetailRemoteSource, popularMoviesRemoteDataSource, localDataSource) // Updated

        // act
        val result = repository.getMovieByTitle("Fight Club")

        // assert
        assertEquals("Fight Club", result?.title)
        assertEquals(42, result?.id)
        assertEquals(1, movieDetailRemoteSource.getMovieByTitleCalls)
    }

    @Test
    fun `getMovieByTitle should return null when movie not found`() = runTest {
        // arrange
        val localDataSource = FakeMoviesLocalDataSource()
        val popularMoviesRemoteDataSource = FakeMoviesRemoteDataSource(Result.success(emptyList()))
        val movieDetailRemoteSource = FakeMovieDetailRemoteSource(movieToReturn = createMovie(title = "Another Movie")) // Movie that won't match
        val repository = MoviesRepositoryImpl(movieDetailRemoteSource, popularMoviesRemoteDataSource, localDataSource)

        // act
        val result = repository.getMovieByTitle("NonExistentMovie")

        // assert
        assertNull(result)
        assertEquals(1, movieDetailRemoteSource.getMovieByTitleCalls)
    }

    @Test
    fun `getMovieByTitle should return null when remote throws exception`() = runTest {
        // arrange
        val localDataSource = FakeMoviesLocalDataSource()
        val popularMoviesRemoteDataSource = FakeMoviesRemoteDataSource(Result.success(emptyList()))
        val movieDetailRemoteSource = FakeMovieDetailRemoteSource(exceptionToThrow = Exception("Network error"))
        val repository = MoviesRepositoryImpl(movieDetailRemoteSource, popularMoviesRemoteDataSource, localDataSource)

        // act
        val result = repository.getMovieByTitle("Any Movie")

        // assert
        assertNull(result)
        assertEquals(1, movieDetailRemoteSource.getMovieByTitleCalls)
    }
}