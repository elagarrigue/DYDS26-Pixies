package edu.dyds.movies.presentation.detail

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.fakes.FakeGetMovieByTitleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testScope = CoroutineScope(UnconfinedTestDispatcher())

    private fun sampleMovie(id: Int = 1, title: String = "Title $id"): Movie {
        return Movie(
            id = id,
            title = title,
            overview = "Overview $id",
            releaseDate = "2025-01-01",
            poster = "poster_$id.jpg",
            backdrop = null,
            originalTitle = title,
            originalLanguage = "en",
            popularity = 12.0,
            voteAverage = 7.7
        )
    }

    @Test
    fun `getMovieByTitle emits loading then success state`() = runTest {
        // arrange
        val movie = sampleMovie(42, "The Answer")
        val useCase = FakeGetMovieByTitleUseCase(movie)
        val viewModel = DetailViewModel(useCase)

        val states = mutableListOf<DetailViewModel.MovieDetailUiState>()

        testScope.launch() {
            viewModel.movieDetailStateFlow.collect { state ->
                states.add(state)
            }
        }

        // act
        viewModel.getMovieByTitle(movie.title)

        // assert
        assertEquals(DetailViewModel.MovieDetailUiState(), states.first())

        val loadingState = states[1]
        assertNotNull(loadingState, "Expected a loading state to be emitted")
        assertEquals(loadingState.movie, null)

        val successState = states[2]
        assertFalse(successState.isLoading)
        assertEquals(movie, successState.movie)
        assertEquals(1, useCase.invokeCalls)
    }

    @Test
    fun `getMovieByTitle handles missing movie`() = runTest {
        // arrange
        val useCase = FakeGetMovieByTitleUseCase(null)
        val viewModel = DetailViewModel(useCase)

        val states = mutableListOf<DetailViewModel.MovieDetailUiState>()
        testScope.launch() {
            viewModel.movieDetailStateFlow.collect { states.add(it) }
        }

        // act
        viewModel.getMovieByTitle("NonExistentMovie")
        advanceUntilIdle()

        // assert
        assertEquals(DetailViewModel.MovieDetailUiState(), states.first())

        val finalState = states.last()
        assertFalse(finalState.isLoading)
        assertEquals(null, finalState.movie)
        assertEquals(1, useCase.invokeCalls)
    }
}