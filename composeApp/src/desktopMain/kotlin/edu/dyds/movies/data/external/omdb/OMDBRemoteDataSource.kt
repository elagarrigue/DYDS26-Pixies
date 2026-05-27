package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.data.external.MovieRemoteDataSource
import edu.dyds.movies.data.external.RemoteMovie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class OMDBRemoteDataSource(
    private val omdbHttpClient: HttpClient
) : MovieRemoteDataSource {

    override suspend fun getMovieByTitle(title: String): RemoteMovie {
        return getOMDBMovieDetails(title)
    }

    suspend fun getOMDBMovieDetails(title: String): RemoteMovie {
        return omdbHttpClient.get("/?t=$title").body()
    }
}
