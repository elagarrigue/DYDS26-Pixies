package edu.dyds.movies.data

import edu.dyds.movies.data.external.MoviesRemoteDataSource
import edu.dyds.movies.data.external.toDomainMovie
import edu.dyds.movies.data.local.MoviesLocalDataSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(
    private val remoteDataSource: MoviesRemoteDataSource,
    private val localDataSource: MoviesLocalDataSource
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        val cachedMovies = localDataSource.getPopularMovies()
        if (cachedMovies.isNotEmpty()) {
            return cachedMovies
        }

        return try {
            remoteDataSource
                .getPopularMovies()
                .map { remoteMovie -> remoteMovie.toDomainMovie() }
                .also { movies -> localDataSource.savePopularMovies(movies) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieByTitle(title: String): Movie? {
        return try {
            remoteDataSource.getMovieByTitle(title).toDomainMovie()
        } catch (_: Exception) {
            null
        }
    }
}
