package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.PopularMoviesRemoteSource
import edu.dyds.movies.domain.entity.Movie

class FakeMoviesRemoteDataSource(
    private val moviesResult: Result<List<Movie>>
) : PopularMoviesRemoteSource {
    override suspend fun getPopularMovies(): List<Movie> {
        return moviesResult.getOrThrow()
    }
}