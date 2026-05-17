package edu.dyds.movies.data.fakes

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class FakeMoviesRepository : MoviesRepository {
    private val movieDatabase = mutableMapOf<Int, Movie>()
    var getMovieDetailsCallCount = 0
        private set

    fun addMovie(movie: Movie) {
        movieDatabase[movie.id] = movie
    }

    override suspend fun getPopularMovies(): List<Movie> {
        return movieDatabase.values.toList()
    }

    override suspend fun getMovieDetails(id: Int): Movie? {
        getMovieDetailsCallCount++
        return movieDatabase[id]
    }
}
