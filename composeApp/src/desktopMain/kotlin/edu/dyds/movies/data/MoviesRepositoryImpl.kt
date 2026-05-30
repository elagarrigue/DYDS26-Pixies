package edu.dyds.movies.data

import edu.dyds.movies.data.external.MovieDetailRemoteSource
import edu.dyds.movies.data.external.PopularMoviesRemoteSource
import edu.dyds.movies.data.local.MoviesLocalDataSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(
    private val movieDetailRemoteSource: MovieDetailRemoteSource,
    private val popularMoviesRemoteSource: PopularMoviesRemoteSource,
    private val localDataSource: MoviesLocalDataSource
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        val cachedMovies = localDataSource.getPopularMovies()
        if (cachedMovies.isNotEmpty()) {
            return cachedMovies
        }

        return try {
            popularMoviesRemoteSource
                .getPopularMovies()
                .also { movies -> localDataSource.savePopularMovies(movies) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieByTitle(title: String): Movie {
        return movieDetailRemoteSource.getMovieByTitle(title)
    }
}