package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.MoviesRemoteDataSource
import edu.dyds.movies.data.external.tmdb.RemoteMovie

class FakeMoviesRemoteDataSource(
    private val moviesResult: Result<List<RemoteMovie>>
) : MoviesRemoteDataSource {
    override suspend fun getPopularMovies(): List<RemoteMovie> {
        return moviesResult.getOrThrow()
    }

    override suspend fun getMovieDetails(id: Int): RemoteMovie {
        return moviesResult.getOrThrow().find { it.id == id }
            ?: throw IllegalArgumentException("Movie not found")
    }
}

