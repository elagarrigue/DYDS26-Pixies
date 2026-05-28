package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository

class GetMovieByTitleUseCaseImpl(
    private val moviesRepository: MoviesRepository
) : GetMovieByTitleUseCase {
    override suspend operator fun invoke(title: String): Movie {
        return moviesRepository.getMovieByTitle(title)
    }
}

