package edu.dyds.movies.data.fakes

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class FakeMoviesRepository : MoviesRepository {
    private val movies: List<Movie>
    var getPopularMoviesCalls = 0
        private set
    var getMovieByTitleCallCount = 0
        private set

    constructor(movies: List<Movie> = emptyList()) {
        this.movies = movies
    }

    override suspend fun getPopularMovies(): List<Movie> {
        getPopularMoviesCalls++
        return movies
    }

    override suspend fun getMovieByTitle(title: String): Movie? {
        getMovieByTitleCallCount++
        for (movie in movies) {
            if (movie.title == title) {
                return movie
            }
        }
        return null
    }
}