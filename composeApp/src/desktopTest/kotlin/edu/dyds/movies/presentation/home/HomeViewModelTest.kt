package edu.dyds.movies.presentation.home

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.fakes.FakeGetPopularMoviesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testScope = CoroutineScope(UnconfinedTestDispatcher())

    private fun qualifiedMovie(id: Int, title: String): QualifiedMovie {
        return QualifiedMovie(
            movie = Movie(
                id = id,
                title = title,
                overview = "Overview $id",
                releaseDate = "2025-01-01",
                poster = "poster_$id.jpg",
                backdrop = null,
                originalTitle = title,
                originalLanguage = "en",
                popularity = 100.0,
                voteAverage = 8.0
            ),
            isGoodMovie = true
        )
    }

    @Test
    fun `getAllMovies emits loading then success state`() = runTest {
        // arrange
        val movies = listOf(
            qualifiedMovie(1, "Movie 1"),
            qualifiedMovie(2, "Movie 2")
        )
        val useCase = FakeGetPopularMoviesUseCase(movies)
        val viewModel = HomeViewModel(useCase)

        val states = mutableListOf<HomeViewModel.MoviesUiState>()

        testScope.launch {
            viewModel.moviesStateFlow.collect { state ->
                states.add(state)
            }
        }

        // act
        viewModel.getAllMovies()
        advanceUntilIdle()

        // assert
        assertEquals(HomeViewModel.MoviesUiState(), states.first())

        val loadingState = states.find { it.isLoading }
        assertTrue(loadingState != null, "Expected a loading state to be emitted")
        assertTrue(loadingState.movies.isEmpty())

        val successState = states.last()
        assertFalse(successState.isLoading)
        assertEquals(movies, successState.movies)

        assertEquals(1, useCase.invokeCalls)
    }
}