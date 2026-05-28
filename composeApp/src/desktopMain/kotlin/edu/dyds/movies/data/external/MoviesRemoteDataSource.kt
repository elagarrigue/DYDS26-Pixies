package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Movie

interface MoviesRemoteDataSource {

    suspend fun getPopularMovies(): List<Movie>

}

interface MovieRemoteDataSource {

    suspend fun getMovieByTitle(title: String): Movie

}