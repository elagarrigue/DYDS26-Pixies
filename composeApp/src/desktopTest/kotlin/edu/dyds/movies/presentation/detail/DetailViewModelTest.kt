package edu.dyds.movies.presentation.detail

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.fakes.FakeGetMovieDetailsUseCase
import edu.dyds.movies.domain.usecase.GetMovieDetailsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


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

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.movieDetailStateFlow.collect { state ->
                states.add(state)
            }
        }

        // initial state
        assertEquals(DetailViewModel.MovieDetailUiState(), states.first())

        // act
        viewModel.getMovieDetail(movie.id)
        advanceUntilIdle()

        // assert loading state emitted
        val loadingState = states.find { it.isLoading }!!
        assertTrue(loadingState != null, "Expected a loading state to be emitted")
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
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.movieDetailStateFlow.collect { states.add(it) }
        }

        // initial state
        assertEquals(DetailViewModel.MovieDetailUiState(), states.first())

        // act
        viewModel.getMovieDetail(123)
        advanceUntilIdle()

        // assert final state has no movie and is not loading
        val finalState = states.last()
        assertFalse(finalState.isLoading)
        assertEquals(null, finalState.movie)
        assertEquals(1, useCase.invokeCalls)
    }
}

