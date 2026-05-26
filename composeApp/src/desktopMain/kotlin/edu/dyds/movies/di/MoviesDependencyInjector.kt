package edu.dyds.movies.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.dyds.movies.data.MoviesRepositoryImpl
import edu.dyds.movies.data.external.tmdb.TMDBRemoteDataSource
import edu.dyds.movies.data.local.MoviesLocalDataSourceImpl
import edu.dyds.movies.domain.usecase.GetMovieByTitleUseCase
import edu.dyds.movies.domain.usecase.GetMovieByTitleUseCaseImpl
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCaseImpl
import edu.dyds.movies.presentation.home.HomeViewModel
import edu.dyds.movies.presentation.detail.DetailViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val API_KEY = "d18da1b5da16397619c688b0263cd281"

object MoviesDependencyInjector {

    private val tmdbHttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.themoviedb.org"
                parameters.append("api_key", API_KEY)
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
    }

    private val moviesRepository by lazy {
        MoviesRepositoryImpl(
            remoteDataSource = TMDBRemoteDataSource(tmdbHttpClient),
            localDataSource = MoviesLocalDataSourceImpl()
        )
    }

    private val getPopularMoviesUseCase: GetPopularMoviesUseCase by lazy {
        GetPopularMoviesUseCaseImpl(moviesRepository)
    }

    private val getMovieByTitleUseCase: GetMovieByTitleUseCase by lazy {
        GetMovieByTitleUseCaseImpl(moviesRepository)
    }

    @Composable
    fun getHomeViewModel(): HomeViewModel {
        return viewModel {
            HomeViewModel(
                getPopularMoviesUseCase = getPopularMoviesUseCase
            )
        }
    }

    @Composable
    fun getDetailViewModel(): DetailViewModel {
        return viewModel {
            DetailViewModel(
                getMovieByTitleUseCase = getMovieByTitleUseCase
            )
        }
    }
}