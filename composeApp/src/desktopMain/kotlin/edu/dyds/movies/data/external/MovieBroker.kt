package edu.dyds.movies.data.external

import edu.dyds.movies.data.external.omdb.OMDBRemoteDataSource
import edu.dyds.movies.data.external.tmdb.TMDBRemoteDataSource
import edu.dyds.movies.domain.entity.Movie

class MovieBroker(
    private val tmdbDataSource: MovieDetailRemoteSource,
    private val omdbDataSource: MovieDetailRemoteSource
) : MovieDetailRemoteSource {

    override suspend fun getMovieByTitle(title: String): Movie? {
        val tmdbMovie = tmdbDataSource.getMovieByTitle(title)
        val omdbMovie = omdbDataSource.getMovieByTitle(title)

        return when {
            tmdbMovie != null && omdbMovie != null -> buildMovie(tmdbMovie, omdbMovie)
            tmdbMovie != null -> tmdbMovie.copy(overview = "TMDB: ${tmdbMovie.overview}")
            omdbMovie != null -> omdbMovie.copy(overview = "OMDB: ${omdbMovie.overview}")
            else -> null
        }
    }

    private fun buildMovie(tmdbMovie: Movie, omdbMovie: Movie ): Movie =
        Movie(
            id = tmdbMovie.id,
            title = tmdbMovie.title,
            overview = listOfNotNull(
                if (tmdbMovie.overview.isNotEmpty()) "TMDB: ${tmdbMovie.overview}" else null,
                if (omdbMovie.overview.isNotEmpty()) "OMDB: ${omdbMovie.overview}" else null
            ).joinToString("\n\n"),
            releaseDate = tmdbMovie.releaseDate,
            poster = tmdbMovie.poster,
            backdrop = tmdbMovie.backdrop,
            originalTitle = tmdbMovie.originalTitle,
            originalLanguage = tmdbMovie.originalLanguage,
            popularity = (tmdbMovie.popularity + omdbMovie.popularity) / 2.0,
            voteAverage = (tmdbMovie.voteAverage + omdbMovie.voteAverage) / 2.0
        )
}
