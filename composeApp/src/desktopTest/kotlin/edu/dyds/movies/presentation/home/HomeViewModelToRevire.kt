package edu.dyds.movies.presentation.home

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.fakes.FakeGetPopularMoviesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTestToReview {

    private val testScope = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testScope)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

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
    fun `getAllMovies emits loading then success state`() = runTest(testScope) {
        // arrange
        val movies = listOf(
            qualifiedMovie(1, "Movie 1"),
            qualifiedMovie(2, "Movie 2")
        )
        val useCase = FakeGetPopularMoviesUseCase(movies)
        val viewModel = HomeViewModel(useCase)

        val states: ArrayList<HomeViewModel.MoviesUiState> = arrayListOf()

        backgroundScope.launch {
            viewModel.moviesStateFlow.collect { state ->
                println("Recolecto el estado: $state")
                states.add(state)
            }
        }

        // act
        viewModel.getAllMovies()

        // assert
        assertTrue(states.size >= 3, "Expected at least three states (previous, loading, success)")

        val firstState = states[1]
        assertTrue(firstState.isLoading, "Expected first emitted state to be loading")

        val secondState = states[2]
        assertFalse(secondState.isLoading, "Expected second emitted state not loading")
        assertEquals(movies, secondState.movies)

        assertEquals(1, useCase.invokeCalls)
    }
}