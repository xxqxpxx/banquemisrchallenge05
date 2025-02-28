package com.banquemisr.challenge05.presentation.movielist

import androidx.lifecycle.ViewModel
import com.banquemisr.challenge05.domain.usecase.GetNowPlayingMoviesUseCase
import com.banquemisr.challenge05.domain.usecase.GetPopularMoviesUseCase
import com.banquemisr.challenge05.domain.usecase.GetUpcomingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase,
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase
) : ViewModel() {

}