package com.banquemisr.challenge05.domain.usecase

import com.banquemisr.challenge05.domain.models.MovieDetail
import com.banquemisr.challenge05.domain.repo.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int, forceRefresh: Boolean = false): Flow<MovieDetail?> {
        return repository.getMovieDetail(movieId, forceRefresh)
    }
}