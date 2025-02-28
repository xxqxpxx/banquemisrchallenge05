package com.banquemisr.challenge05.domain.usecase

import com.banquemisr.challenge05.domain.models.Movie
import com.banquemisr.challenge05.domain.repo.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUpcomingMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Flow<List<Movie>> {
        return repository.getUpcomingMovies(forceRefresh)
    }
}