package com.banquemisr.challenge05.presentation.movies.moviedetails

import androidx.compose.runtime.Stable
import com.banquemisr.challenge05.domain.models.MovieDetail

class MovieDetailContract {

    @Stable
    data class State(
        val movieDetail: MovieDetail? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class Event {
        data class LoadMovieDetail(val movieId: Int) : Event()
        object RefreshMovieDetail : Event()
        object NavigateBack : Event()
    }

    sealed class Effect {
        object NavigateBack : Effect()
        data class ShowSnackbar(val message: String) : Effect()
    }
}