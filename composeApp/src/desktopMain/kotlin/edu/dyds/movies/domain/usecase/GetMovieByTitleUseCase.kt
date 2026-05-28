
package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie

interface GetMovieByTitleUseCase {
    suspend operator fun invoke(title: String): Movie
}


