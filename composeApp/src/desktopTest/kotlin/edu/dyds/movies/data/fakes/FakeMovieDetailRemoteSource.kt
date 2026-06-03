package edu.dyds.movies.data.fakes

import edu.dyds.movies.data.external.MovieDetailRemoteSource
import edu.dyds.movies.domain.entity.Movie

class FakeMovieDetailRemoteSource(
    private val movieToReturn: Movie? = null,
    private val exceptionToThrow: Exception? = null
) : MovieDetailRemoteSource {

    var getMovieByTitleCalls = 0
        private set

    override suspend fun getMovieByTitle(title: String): Movie? {
        getMovieByTitleCalls++
        exceptionToThrow?.let { return null }
        return movieToReturn?.takeIf { it.title == title }
    }
}