package com.banquemisr.challenge05.presentation.movies.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banquemisr.challenge05.domain.usecase.GetNowPlayingMoviesUseCase
import com.banquemisr.challenge05.domain.usecase.GetPopularMoviesUseCase
import com.banquemisr.challenge05.domain.usecase.GetUpcomingMoviesUseCase
import com.banquemisr.challenge05.presentation.movies.movielist.MovieListContract.Effect
import com.banquemisr.challenge05.presentation.movies.movielist.MovieListContract.Event
import com.banquemisr.challenge05.presentation.movies.movielist.MovieListContract.Tab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase,
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase
) : ViewModel() {


    private val _state = MutableStateFlow(MovieListContract.State())
    val state: StateFlow<MovieListContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()


    init {
        onEvent(Event.LoadNowPlayingMovies)
        onEvent(Event.LoadPopularMovies)
        onEvent(Event.LoadUpcomingMovies)
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.LoadNowPlayingMovies -> {
                loadNowPlayingMovies()
            }

            is Event.LoadPopularMovies -> {
                loadPopularMovies()
            }

            is Event.LoadUpcomingMovies -> {
                loadUpcomingMovies()
            }

            is Event.SelectTab -> {
                _state.update { it.copy(selectedTab = event.tab) }
            }

            is Event.MovieClicked -> {
                viewModelScope.launch {
                    _effect.emit(Effect.NavigateToMovieDetail(event.movieId))
                }
            }

            is Event.RefreshMovies -> {
                when (event.tab) {
                    Tab.NOW_PLAYING -> loadNowPlayingMovies(true)
                    Tab.POPULAR -> loadPopularMovies(true)
                    Tab.UPCOMING -> loadUpcomingMovies(true)
                }
            }
        }
    }

    private fun loadNowPlayingMovies(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(nowPlayingIsLoading = true, nowPlayingError = null) }

            getNowPlayingMoviesUseCase(forceRefresh).onEach { movies ->
                _state.update {
                    it.copy(
                        nowPlayingMovies = movies, nowPlayingIsLoading = false
                    )
                }
            }.catch { exception ->
                _state.update {
                    it.copy(
                        nowPlayingIsLoading = false,
                        nowPlayingError = exception.message ?: "An error occurred"
                    )
                }
                _effect.emit(Effect.ShowSnackbar("Failed to load Now Playing movies"))
            }.launchIn(this)
        }
    }

    private fun loadPopularMovies(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(popularIsLoading = true, popularError = null) }

            getPopularMoviesUseCase(forceRefresh).onEach { movies ->
                _state.update { it.copy(popularMovies = movies, popularIsLoading = false) }
            }.catch { exception ->
                _state.update {
                    it.copy(
                        popularIsLoading = false,
                        popularError = exception.message ?: "An error occurred"
                    )
                }
                _effect.emit(Effect.ShowSnackbar("Failed to load Popular movies"))
            }.launchIn(this)
        }
    }

    private fun loadUpcomingMovies(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(upcomingIsLoading = true, upcomingError = null) }

            getUpcomingMoviesUseCase(forceRefresh).onEach { movies ->
                _state.update { it.copy(upcomingMovies = movies, upcomingIsLoading = false) }
            }.catch { exception ->
                _state.update {
                    it.copy(
                        upcomingIsLoading = false,
                        upcomingError = exception.message ?: "An error occurred"
                    )
                }
                _effect.emit(Effect.ShowSnackbar("Failed to load Upcoming movies"))
            }.launchIn(this)
        }
    }
}