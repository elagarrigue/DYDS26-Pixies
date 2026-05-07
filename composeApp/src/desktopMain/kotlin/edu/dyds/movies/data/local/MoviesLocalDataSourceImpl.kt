package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie

class MoviesLocalDataSourceImpl : MoviesLocalDataSource {
    private val popularMovies: MutableList<Movie> = mutableListOf()

    override fun getPopularMovies(): List<Movie> {
        return popularMovies.toList()
    }

    override fun savePopularMovies(movies: List<Movie>) {
        popularMovies.clear()
        popularMovies.addAll(movies)
    }
}
