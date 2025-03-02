package com.banquemisr.challenge05.presentation.movies.moviedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banquemisr.challenge05.domain.usecase.GetMovieDetailUseCase
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

import com.banquemisr.challenge05.presentation.movies.moviedetails.MovieDetailContract.*

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    private var movieId: Int = checkNotNull(savedStateHandle["movieId"])

    init {
        onEvent(Event.LoadMovieDetail(movieId))
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.LoadMovieDetail -> {
                loadMovieDetail(event.movieId)
            }

            is Event.RefreshMovieDetail -> {
                loadMovieDetail(movieId, true)
            }

            is Event.NavigateBack -> {
                viewModelScope.launch {
                    _effect.emit(Effect.NavigateBack)
                }
            }
        }
    }

    private fun loadMovieDetail(id: Int, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getMovieDetailUseCase(id, forceRefresh)
                .onEach { movieDetail ->
                    if (movieDetail != null) {
                        _state.update { it.copy(movieDetail = movieDetail, isLoading = false) }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Movie detail not found not available offline, refreshing"
                            )
                        }

                        loadMovieDetail(movieId, true)
                    }
                }
                .catch { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "An error occurred"
                        )
                    }
                    _effect.emit(Effect.ShowSnackbar("Failed to load movie details"))
                }
                .launchIn(this)
        }
    }
}