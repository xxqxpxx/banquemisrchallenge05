package com.banquemisr.challenge05.data.repo

import com.banquemisr.challenge05.data.local.GenreEntity
import com.banquemisr.challenge05.data.local.MovieDao
import com.banquemisr.challenge05.data.local.MovieDetailEntity
import com.banquemisr.challenge05.data.local.MovieEntity
import com.banquemisr.challenge05.data.remote.Api
import com.banquemisr.challenge05.data.remote.models.GenreDto
import com.banquemisr.challenge05.data.remote.models.MovieDetailDto
import com.banquemisr.challenge05.data.remote.models.MovieDto
import com.banquemisr.challenge05.domain.models.Movie
import com.banquemisr.challenge05.domain.models.MovieDetail
import com.banquemisr.challenge05.domain.repo.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val movieApi: Api,
    private val movieDao: MovieDao
) : MovieRepository {

    override suspend fun getNowPlayingMovies(forceRefresh: Boolean): Flow<List<Movie>> {
        return getCachedMovies("now_playing", forceRefresh) {
            val response = movieApi.getNowPlayingMovies()
            response.results.map { it.toMovieEntity("now_playing") }
        }
    }

    override suspend fun getPopularMovies(forceRefresh: Boolean): Flow<List<Movie>> {
        return getCachedMovies("popular", forceRefresh) {
            val response = movieApi.getPopularMovies()
            response.results.map { it.toMovieEntity("popular") }
        }
    }

    override suspend fun getUpcomingMovies(forceRefresh: Boolean): Flow<List<Movie>> {
        return getCachedMovies("upcoming", forceRefresh) {
            val response = movieApi.getUpcomingMovies()
            response.results.map { it.toMovieEntity("upcoming") }
        }
    }

    override suspend fun getMovieDetail(movieId: Int, forceRefresh: Boolean): Flow<MovieDetail?> {
        // Check if data should be refreshed
        val shouldFetch = forceRefresh || movieDao.getMovieById(movieId) == null

        if (shouldFetch) {
            try {
                val remoteMovie = movieApi.getMovieDetail(movieId)
                movieDao.insertMovieDetail(remoteMovie.toMovieDetailEntity())
            } catch (e: Exception) {
                // Error fetching from remote, we'll try to use cache if available
            }
        }

        return movieDao.getMovieDetail(movieId).map { movieDetailEntity ->
            movieDetailEntity?.toMovieDetail()
        }
    }

    private suspend fun getCachedMovies(
        category: String,
        forceRefresh: Boolean,
        fetchFromRemote: suspend () -> List<MovieEntity>
    ): Flow<List<Movie>> {
        // Check if we should fetch fresh data
        val shouldFetch = forceRefresh || movieDao.getMoviesCountByCategory(category) == 0

        if (shouldFetch) {
            try {
                val remoteMovies = fetchFromRemote()
                movieDao.deleteMoviesByCategory(category)
                movieDao.insertMovies(remoteMovies)
            } catch (e: Exception) {
                // Error fetching from remote, we'll try to use cache if available
            }
        }

        return movieDao.getMoviesByCategory(category).map { movieEntities ->
            movieEntities.map { it.toMovie() }
        }
    }

    // Mappers
    private fun MovieDto.toMovieEntity(category: String): MovieEntity {
        return MovieEntity(
            id = id,
            title = title,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            voteAverage = voteAverage,
            voteCount = voteCount,
            releaseDate = releaseDate,
            popularity = popularity,
            category = category
        )
    }

    private fun MovieEntity.toMovie(): Movie {
        return Movie(
            id = id,
            title = title,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            voteAverage = voteAverage,
            voteCount = voteCount,
            releaseDate = releaseDate
        )
    }

    private fun MovieDetailDto.toMovieDetailEntity(): MovieDetailEntity {
        return MovieDetailEntity(
            id = id,
            title = title,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            voteAverage = voteAverage,
            voteCount = voteCount,
            releaseDate = releaseDate,
            runtime = runtime,
            genres = genres.map { it.toGenreEntity() },
            budget = budget,
            revenue = revenue,
            status = status,
            tagline = tagline,
            popularity = popularity
        )
    }

    private fun GenreDto.toGenreEntity(): GenreEntity {
        return GenreEntity(
            id = id,
            name = name
        )
    }

    private fun MovieDetailEntity.toMovieDetail(): MovieDetail {
        return MovieDetail(
            id = id,
            title = title,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            voteAverage = voteAverage,
            voteCount = voteCount,
            releaseDate = releaseDate,
            runtime = runtime,
            genres = genres.map { it.name },
            budget = budget,
            revenue = revenue,
            status = status,
            tagline = tagline
        )
    }
}