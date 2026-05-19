package edu.dyds.movies.presentation.detail

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.fakes.FakeGetMovieDetailsUseCase
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
import kotlin.test.assertTrue

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
    fun `getMovieDetail emits loading then success state`() = runTest {
        // arrange
        val movie = sampleMovie(42, "The Answer")
        val useCase = FakeGetMovieDetailsUseCase(movie)
        val viewModel = DetailViewModel(useCase)

        val states = mutableListOf<DetailViewModel.MovieDetailUiState>()

        testScope.launch() {
            viewModel.movieDetailStateFlow.collect { state ->
                states.add(state)
            }
        }

        // act
        viewModel.getMovieDetail(movie.id)
        advanceUntilIdle()


        // assert
        assertEquals(DetailViewModel.MovieDetailUiState(), states.first())

        val loadingState = states.find { it.isLoading }
        assertNotNull(loadingState, "Expected a loading state to be emitted")
        assertTrue(loadingState.movie == null)

        val successState = states.last()
        assertFalse(successState.isLoading)
        assertEquals(movie, successState.movie)
        assertEquals(1, useCase.invokeCalls)
    }

    @Test
    fun `getMovieDetail handles missing movie`() = runTest {
        // arrange
        val useCase = FakeGetMovieDetailsUseCase(null)
        val viewModel = DetailViewModel(useCase)

        val states = mutableListOf<DetailViewModel.MovieDetailUiState>()
        testScope.launch() {
            viewModel.movieDetailStateFlow.collect { states.add(it) }
        }

        // act
        viewModel.getMovieDetail(123)
        //delay(10)
        advanceUntilIdle()

        // assert
        assertEquals(DetailViewModel.MovieDetailUiState(), states.first())

        val finalState = states.last()
        assertFalse(finalState.isLoading)
        assertEquals(null, finalState.movie)
        print(useCase.invokeCalls)
        assertEquals(1, useCase.invokeCalls)
    }
}
