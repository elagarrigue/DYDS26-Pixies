package edu.dyds.movies.presentation.home

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    class GetPopularMoviesUseCaseFake : GetPopularMoviesUseCase {
        var movieList: List<QualifiedMovie> = emptyList()

        override suspend fun invoke(): List<QualifiedMovie> = movieList
    }

    private fun createTestMovie(id: Int = 1): QualifiedMovie {
        return QualifiedMovie(
            movie = Movie(
                id = id,
                title = "Test Movie $id",
                overview = "Overview $id",
                releaseDate = "2024-01-01",
                poster = "/poster$id.jpg",
                backdrop = "/backdrop$id.jpg",
                originalTitle = "Original $id",
                originalLanguage = "en",
                popularity = 8.5,
                voteAverage = 7.5
            ),
            isGoodMovie = true
        )
    }

    @Test
    fun `initial state has no movies and not loading`() = runTest {
        // arrange
        val useCase = GetPopularMoviesUseCaseFake()
        val viewModel = HomeViewModel(useCase)

        // act
        val states = viewModel.moviesStateFlow.take(1).toList()

        // assert
        assertEquals(1, states.size)
        assertFalse(states[0].isLoading)
        assertEquals(emptyList(), states[0].movies)
    }

    @Test
    fun `getAllMovies emits loading state first`() = runTest {
        // arrange
        val useCase = GetPopularMoviesUseCaseFake()
        val viewModel = HomeViewModel(useCase)
        var states = listOf<HomeViewModel.MoviesUiState>()

        // act
        launch {
            states = viewModel.moviesStateFlow.take(3).toList()
        }
        advanceUntilIdle()
        viewModel.getAllMovies()
        advanceUntilIdle()

        // assert
        assertTrue(states.size >= 2, "Should have at least 2 states")
        assertTrue(states[1].isLoading, "Second state should have isLoading = true")
        assertEquals(emptyList(), states[1].movies, "Loading state should have no movies")
    }

    @Test
    fun `getAllMovies emits movies after loading`() = runTest {
        // arrange
        val testMovies = listOf(createTestMovie(1), createTestMovie(2))
        val useCase = GetPopularMoviesUseCaseFake()
        useCase.movieList = testMovies
        val viewModel = HomeViewModel(useCase)
        var states = listOf<HomeViewModel.MoviesUiState>()

        // act
        launch {
            states = viewModel.moviesStateFlow.take(4).toList()
        }
        advanceUntilIdle()
        viewModel.getAllMovies()
        advanceUntilIdle()

        // assert
        assertTrue(states.size >= 3, "Should have at least 3 states")
        assertFalse(states[2].isLoading, "Final state should have isLoading = false")
        assertEquals(testMovies, states[2].movies, "Final state should contain movies from use case")
    }

    @Test
    fun `getAllMovies respects use case return value`() = runTest {
        // arrange
        val movie1 = createTestMovie(42)
        val movie2 = createTestMovie(99)
        val testMovies = listOf(movie1, movie2)
        val useCase = GetPopularMoviesUseCaseFake()
        useCase.movieList = testMovies
        val viewModel = HomeViewModel(useCase)
        var states = listOf<HomeViewModel.MoviesUiState>()

        // act
        launch {
            states = viewModel.moviesStateFlow.take(4).toList()
        }
        advanceUntilIdle()
        viewModel.getAllMovies()
        advanceUntilIdle()

        // assert
        val finalState = states.last()
        assertEquals(2, finalState.movies.size)
        assertEquals(movie1, finalState.movies[0])
        assertEquals(movie2, finalState.movies[1])
    }

    @Test
    fun `getAllMovies handles empty movie list`() = runTest {
        // arrange
        val useCase = GetPopularMoviesUseCaseFake()
        useCase.movieList = emptyList()
        val viewModel = HomeViewModel(useCase)
        var states = listOf<HomeViewModel.MoviesUiState>()

        // act
        launch {
            states = viewModel.moviesStateFlow.take(4).toList()
        }
        advanceUntilIdle()
        viewModel.getAllMovies()
        advanceUntilIdle()

        // assert
        assertTrue(states.size >= 3, "Should have at least 3 states")
        val finalState = states.last()
        assertFalse(finalState.isLoading, "Final state should not be loading")
        assertEquals(emptyList(), finalState.movies, "Final state should have empty movies list")
    }
}