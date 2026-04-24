package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie

class InMemoryMoviesCache {
    private val popularMovies: MutableList<Movie> = mutableListOf()

    fun getPopularMovies(): List<Movie> {
        return popularMovies.toList()
    }

    fun savePopularMovies(movies: List<Movie>) {
        popularMovies.clear()
        popularMovies.addAll(movies)
    }
}

