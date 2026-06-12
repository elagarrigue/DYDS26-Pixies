package edu.dyds.movies.data.external.tmdb

import edu.dyds.movies.data.external.MovieDetailRemoteSource
import edu.dyds.movies.data.external.PopularMoviesRemoteSource
import edu.dyds.movies.domain.entity.Movie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class TMDBRemoteDataSource(
    private val tmdbHttpClient: HttpClient
) : MovieDetailRemoteSource, PopularMoviesRemoteSource {
    override suspend fun getPopularMovies(): List<Movie> {
        return tmdbHttpClient.get("/3/discover/movie?sort_by=popularity.desc")
            .body<RemoteResult>()
            .results
            .map { it.toDomainMovie() }
    }

    override suspend fun getMovieByTitle(title: String): Movie? {
        return  try{
            tmdbHttpClient.get("/3/search/movie?query=$title")
                .body<RemoteResult>()
                .results
                .first().toDomainMovie()
        } catch (_: Exception) {
            null
        }
    }
}