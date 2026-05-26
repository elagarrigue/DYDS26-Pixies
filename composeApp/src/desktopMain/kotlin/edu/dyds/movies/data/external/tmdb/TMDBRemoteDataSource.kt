package edu.dyds.movies.data.external.tmdb

import edu.dyds.movies.data.external.MovieRemoteDataSource
import edu.dyds.movies.data.external.MoviesRemoteDataSource
import edu.dyds.movies.data.external.RemoteMovie
import edu.dyds.movies.data.external.RemoteResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class TMDBRemoteDataSource(
    private val tmdbHttpClient: HttpClient
) : MovieRemoteDataSource, MoviesRemoteDataSource {
    override suspend fun getPopularMovies(): List<RemoteMovie> {
        return tmdbHttpClient.get("/3/discover/movie?sort_by=popularity.desc")
            .body<RemoteResult>()
            .results
    }

    override suspend fun getMovieByTitle(title: String): RemoteMovie {
        return tmdbHttpClient.get("/3/search/movie?query=$title")
            .body<RemoteResult>()
            .results
            .first()
    }
}
