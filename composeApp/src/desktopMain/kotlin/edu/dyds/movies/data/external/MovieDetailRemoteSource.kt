package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Movie

interface MovieDetailRemoteSource {

    suspend fun getMovieByTitle(title: String): Movie?

}