package edu.dyds.movies.data

import edu.dyds.movies.data.external.MovieBroker
import edu.dyds.movies.data.local.MoviesLocalDataSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class MoviesRepositoryImpl(
    private val movieBroker: MovieBroker,
    private val localDataSource: MoviesLocalDataSource
) : MoviesRepository {

    override suspend fun getPopularMovies(): List<Movie> {
        val cachedMovies = localDataSource.getPopularMovies()
        if (cachedMovies.isNotEmpty()) {
            return cachedMovies
        }

        return try {
            movieBroker
                .getPopularMovies()
                .also { movies -> localDataSource.savePopularMovies(movies) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieByTitle(title: String): Movie {
        return movieBroker.getMovieByTitle(title)
    }
}