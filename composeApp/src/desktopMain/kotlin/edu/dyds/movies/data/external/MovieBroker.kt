package edu.dyds.movies.data.external

import edu.dyds.movies.data.external.omdb.OMDBRemoteDataSource
import edu.dyds.movies.data.external.tmdb.TMDBRemoteDataSource
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class MovieBroker(
    private val tmdbDataSource: TMDBRemoteDataSource,
    private val omdbDataSource: OMDBRemoteDataSource
) : MovieDetailRemoteSource, PopularMoviesRemoteSource {

    override suspend fun getMovieByTitle(title: String): Movie? = coroutineScope {
        val tmdbMovieDeferred = async {
            tmdbDataSource.getMovieByTitle(title)
        }

        val omdbMovieDeferred = async {
            omdbDataSource.getMovieByTitle(title)
        }

        val tmdbMovie = tmdbMovieDeferred.await()
        val omdbMovie = omdbMovieDeferred.await()

        return@coroutineScope buildMovie(tmdbMovie, omdbMovie)
    }

    override suspend fun getPopularMovies(): List<Movie> {
        return try {
            tmdbDataSource.getPopularMovies()
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun buildMovie(tmdbMovie: Movie, omdbMovie: Movie ): Movie? {
        return Movie(
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
}