package edu.dyds.movies.domain.fakes

import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase

class FakeGetPopularMoviesUseCase(
    private val moviesToReturn: List<QualifiedMovie>
) : GetPopularMoviesUseCase {
    var invokeCalls = 0
        private set

    override suspend fun invoke(): List<QualifiedMovie> {
        invokeCalls++
        return moviesToReturn
    }
}

