package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.data.external.MovieRemoteDataSource
import edu.dyds.movies.domain.entity.Movie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class OMDBRemoteDataSource(
    private val omdbHttpClient: HttpClient
) : MovieRemoteDataSource {

    override suspend fun getMovieByTitle(title: String): Movie {
        return getOMDBMovieDetails(title)
    }

    suspend fun getOMDBMovieDetails(title: String): Movie {
        return omdbHttpClient.get("/?t=$title").body<OMDBRemoteMovie>().toDomainMovie()
    }
}
