package com.banquemisr.challenge05.domain.repo

import com.banquemisr.challenge05.domain.models.Movie
import com.banquemisr.challenge05.domain.models.MovieDetail
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    suspend fun getNowPlayingMovies(forceRefresh: Boolean = false): Flow<List<Movie>>

    suspend fun getPopularMovies(forceRefresh: Boolean = false): Flow<List<Movie>>

    suspend fun getUpcomingMovies(forceRefresh: Boolean = false): Flow<List<Movie>>

    suspend fun getMovieDetail(movieId: Int, forceRefresh: Boolean = false): Flow<MovieDetail?>

}