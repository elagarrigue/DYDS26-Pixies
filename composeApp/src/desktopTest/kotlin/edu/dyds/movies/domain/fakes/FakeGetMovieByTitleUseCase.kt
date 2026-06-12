package edu.dyds.movies.domain.fakes

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.usecase.GetMovieByTitleUseCase

class FakeGetMovieByTitleUseCase(
    private val movieToReturn: Movie?
) : GetMovieByTitleUseCase {
    var invokeCalls = 0
        private set

    override suspend fun invoke(title: String): Movie? {
        invokeCalls++
        return movieToReturn
    }
}