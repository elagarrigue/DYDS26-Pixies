package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.repository.MoviesRepository

private const val MIN_VOTE_AVERAGE = 6.0

class GetPopularMoviesUseCase(
    private val moviesRepository: MoviesRepository
) {
    suspend operator fun invoke(): List<QualifiedMovie> {
        return moviesRepository
            .getPopularMovies()
            .sortedByDescending { movie -> movie.voteAverage }
            .map { movie ->
                QualifiedMovie(
                    movie = movie,
                    isGoodMovie = movie.voteAverage >= MIN_VOTE_AVERAGE
                )
            }
    }
}

