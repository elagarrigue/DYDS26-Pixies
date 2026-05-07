package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie

interface MoviesLocalDataSource {

    fun getPopularMovies(): List<Movie>

    fun savePopularMovies(movies: List<Movie>)

}
