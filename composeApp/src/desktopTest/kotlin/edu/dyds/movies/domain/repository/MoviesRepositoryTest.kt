package edu.dyds.movies.data

import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.fakes.FakeMoviesLocalDataSource
import edu.dyds.movies.data.fakes.FakeMoviesRemoteDataSource
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue


class MoviesRepositoryImplTest {

    private fun createRemoteMovie(id: Int = 1): RemoteMovie {
        return RemoteMovie(
            id = id,
            title = "Test Movie $id",
            overview = "Test Overview",
            releaseDate = "2024-01-01",
            posterPath = "/test$id.jpg",
            backdropPath = "/backdrop$id.jpg",
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
            Movie(
                id = 1,
                title = "Cached Movie",
                overview = "Overview",
                releaseDate = "2024-01-01",
                poster = "https://image.tmdb.org/t/p/w185/test.jpg",
                backdrop = "https://image.tmdb.org/t/p/w780/backdrop.jpg",
                originalTitle = "Original",
                originalLanguage = "en",
                popularity = 7.5,
                voteAverage = 8.0
            )
        )
        val localDataSource = FakeMoviesLocalDataSource().apply {
            savePopularMovies(cached)
        }
        val remoteDataSource = FakeMoviesRemoteDataSource(Result.success(emptyList()))
        val repository = MoviesRepositoryImpl(remoteDataSource, localDataSource)

        // act
        val result = repository.getPopularMovies()

        // assert
        assertEquals(cached, result)
    }

    @Test
    fun `getPopularMovies should fetch from remote when cache is empty`() = runTest {
        // arrange
        val remoteMovies = listOf(
            createRemoteMovie(1),
            createRemoteMovie(2)
        )
        val localDataSource = FakeMoviesLocalDataSource()
        val remoteDataSource = FakeMoviesRemoteDataSource(Result.success(remoteMovies))
        val repository = MoviesRepositoryImpl(remoteDataSource, localDataSource)

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
        val remoteMovies = listOf(createRemoteMovie(1))
        val localDataSource = FakeMoviesLocalDataSource()
        val remoteDataSource = FakeMoviesRemoteDataSource(Result.success(remoteMovies))
        val repository = MoviesRepositoryImpl(remoteDataSource, localDataSource)

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
        val remoteDataSource = FakeMoviesRemoteDataSource(
            Result.failure(Exception("Network error"))
        )
        val repository = MoviesRepositoryImpl(remoteDataSource, localDataSource)

        // act
        val result = repository.getPopularMovies()

        // assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getMovieDetails should return movie when successful`() = runTest {
        // arrange
        val remoteMovie = createRemoteMovie(42)
        val localDataSource = FakeMoviesLocalDataSource()
        val remoteDataSource = FakeMoviesRemoteDataSource(Result.success(listOf(remoteMovie)))
        val repository = MoviesRepositoryImpl(remoteDataSource, localDataSource)

        // act
        val result = repository.getMovieDetails(42)

        // assert
        assertEquals("Test Movie 42", result?.title)
        assertEquals(42, result?.id)
    }

    @Test
    fun `getMovieDetails should return null when remote throws exception`() = runTest {
        // arrange
        val localDataSource = FakeMoviesLocalDataSource()
        val remoteDataSource = FakeMoviesRemoteDataSource(
            Result.failure(Exception("Network error"))
        )
        val repository = MoviesRepositoryImpl(remoteDataSource, localDataSource)

        // act
        val result = repository.getMovieDetails(1)

        // assert
        assertNull(result)
    }
}

