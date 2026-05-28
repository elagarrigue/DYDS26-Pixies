package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.MoviesRemoteDataSource
import edu.dyds.movies.data.external.tmdb.TMDBRemoteMovie

class FakeMoviesRemoteDataSource(
    private val moviesResult: Result<List<TMDBRemoteMovie>>
) : MoviesRemoteDataSource {
    override suspend fun getPopularMovies(): List<TMDBRemoteMovie> {
        return moviesResult.getOrThrow()
    }

    override suspend fun getMovieDetails(id: Int): TMDBRemoteMovie {
        return moviesResult.getOrThrow().find { it.id == id }
            ?: throw IllegalArgumentException("Movie not found")
    }
}

