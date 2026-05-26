package edu.dyds.movies.data.external

interface MoviesRemoteDataSource {

    suspend fun getPopularMovies(): List<RemoteMovie>

}

interface MovieRemoteDataSource {

    suspend fun getMovieByTitle(title: String): RemoteMovie

}