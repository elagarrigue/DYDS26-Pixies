package edu.dyds.movies.domain.fakes

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCase

class FakeGetMovieDetailsUseCase(
    private val movieToReturn: Movie?
) : GetMovieDetailsUseCase {
    var invokeCalls = 0
        private set

    override suspend fun invoke(id: Int): Movie? {
        invokeCalls++
        return movieToReturn
    }
}

