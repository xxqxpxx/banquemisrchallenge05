package com.banquemisr.challenge05.presentation.movies.movielist

import androidx.compose.runtime.Stable
import com.banquemisr.challenge05.domain.models.Movie


/**
 * MVI Contract for the movie list screen
 */
class MovieListContract {

    /**
     * UI State representing the current state of the movie list screen
     */
    @Stable
    data class State(
        val nowPlayingMovies: List<Movie> = emptyList(),
        val popularMovies: List<Movie> = emptyList(),
        val upcomingMovies: List<Movie> = emptyList(),
        val nowPlayingIsLoading: Boolean = false,
        val popularIsLoading: Boolean = false,
        val upcomingIsLoading: Boolean = false,
        val nowPlayingError: String? = null,
        val popularError: String? = null,
        val upcomingError: String? = null,
        val selectedTab: Tab = Tab.NOW_PLAYING
    ) {
        // Helper function to determine if the entire screen is in a loading state
        fun isLoading(): Boolean = nowPlayingIsLoading || popularIsLoading || upcomingIsLoading
        
        // Helper function to get current error message based on selected tab
        fun getCurrentError(): String? {
            return when (selectedTab) {
                Tab.NOW_PLAYING -> nowPlayingError
                Tab.POPULAR -> popularError
                Tab.UPCOMING -> upcomingError
            }
        }
        
        // Helper function to get current movies list based on selected tab
        fun getCurrentMovies(): List<Movie> {
            return when (selectedTab) {
                Tab.NOW_PLAYING -> nowPlayingMovies
                Tab.POPULAR -> popularMovies
                Tab.UPCOMING -> upcomingMovies
            }
        }
    }

    /**
     * Events that user can trigger
     */
    sealed class Event {
        object LoadNowPlayingMovies : Event()
        object LoadPopularMovies : Event()
        object LoadUpcomingMovies : Event()
        data class SelectTab(val tab: Tab) : Event()
        data class MovieClicked(val movieId: Int) : Event()
        data class RefreshMovies(val tab: Tab) : Event()
    }

    /**
     * One-time effects/actions to navigate or show snackbar, etc.
     */
    sealed class Effect {
        data class NavigateToMovieDetail(val movieId: Int) : Effect()
        data class ShowSnackbar(val message: String) : Effect()
    }

    /**
     * Tabs available in the movie list screen
     */
    enum class Tab {
        NOW_PLAYING,
        POPULAR,
        UPCOMING
    }
}