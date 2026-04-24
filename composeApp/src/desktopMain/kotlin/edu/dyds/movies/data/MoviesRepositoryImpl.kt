package edu.dyds.movies.data

import edu.dyds.movies.data.external.TmdbMoviesRemoteDataSource
import edu.dyds.movies.data.external.toDomainMovie
import edu.dyds.movies.data.local.InMemoryMoviesCache
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(
    private val remoteDataSource: TmdbMoviesRemoteDataSource,
    private val memoryCache: InMemoryMoviesCache
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        val cachedMovies = memoryCache.getPopularMovies()
        if (cachedMovies.isNotEmpty()) {
            return cachedMovies
        }

        return try {
            remoteDataSource
                .getPopularMovies()
                .map { remoteMovie -> remoteMovie.toDomainMovie() }
                .also { movies -> memoryCache.savePopularMovies(movies) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieDetails(id: Int): Movie? {
        return try {
            remoteDataSource.getMovieDetails(id).toDomainMovie()
        } catch (_: Exception) {
            null
        }
    }
}

