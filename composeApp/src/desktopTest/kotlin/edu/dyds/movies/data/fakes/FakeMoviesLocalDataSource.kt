package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.local.MoviesLocalDataSource
import edu.dyds.movies.domain.entity.Movie

class FakeMoviesLocalDataSource : MoviesLocalDataSource {
    private var cachedMovies: List<Movie> = emptyList()

    override fun getPopularMovies(): List<Movie> {
        return cachedMovies
    }

    override fun savePopularMovies(movies: List<Movie>) {
        cachedMovies = movies
    }
}

