package edu.dyds.movies.data.external

interface MoviesRemoteDataSource {

    suspend fun getPopularMovies(): List<RemoteMovie>

    suspend fun getMovieByTitle(title: String): RemoteMovie

}
