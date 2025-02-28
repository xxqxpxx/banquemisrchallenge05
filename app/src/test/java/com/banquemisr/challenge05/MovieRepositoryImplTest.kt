package com.banquemisr.challenge05

import com.banquemisr.challenge05.data.local.MovieDao
import com.banquemisr.challenge05.data.local.MovieDetailEntity
import com.banquemisr.challenge05.data.local.MovieEntity
import com.banquemisr.challenge05.data.remote.Api
import com.banquemisr.challenge05.data.remote.models.GenreDto
import com.banquemisr.challenge05.data.remote.models.MovieDetailDto
import com.banquemisr.challenge05.data.remote.models.MovieDto
import com.banquemisr.challenge05.data.remote.models.MovieResponseDto
import com.banquemisr.challenge05.data.repo.MovieRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MovieRepositoryImplTest {

    private lateinit var movieApi: Api
    private lateinit var movieDao: MovieDao
    private lateinit var repository: MovieRepositoryImpl

    @Before
    fun setup() {
        movieApi = mockk()
        movieDao = mockk(relaxed = true)
        repository = MovieRepositoryImpl(movieApi, movieDao)
    }

    @Test
    fun `getNowPlayingMovies returns cached data when available and not forced refresh`() =
        runBlocking {

            val cachedMovies = listOf(
                createMovieEntity(1, "now_playing"),
                createMovieEntity(2, "now_playing")
            )


            coEvery { movieDao.getMoviesCountByCategory("now_playing") } returns 2
            every { movieDao.getMoviesByCategory("now_playing") } returns flowOf(cachedMovies)


            val result = repository.getNowPlayingMovies(forceRefresh = false).first()


            assertEquals(2, result.size)
            assertEquals(1, result[0].id)
            assertEquals(2, result[1].id)
            coVerify(exactly = 0) { movieApi.getNowPlayingMovies() }
        }

    @Test
    fun `getNowPlayingMovies fetches from API when cache is empty`() = runBlocking {


        val apiMovies = listOf(
            createMovieDto(1),
            createMovieDto(2)
        )
        val apiResponse = MovieResponseDto(
            page = 1,
            results = apiMovies,
            totalPages = 1,
            totalResults = 2
        )


        coEvery { movieDao.getMoviesCountByCategory("now_playing") } returns 0
        every { movieDao.getMoviesByCategory("now_playing") } returns flowOf(emptyList())
        coEvery { movieApi.getNowPlayingMovies() } returns apiResponse

        val insertedMovies = slot<List<MovieEntity>>()
        coEvery { movieDao.insertMovies(capture(insertedMovies)) } returns Unit


        val result = repository.getNowPlayingMovies(forceRefresh = false).first()


        assertEquals(0, result.size)
        coVerify { movieApi.getNowPlayingMovies() }
        coVerify { movieDao.insertMovies(any()) }
        assertEquals(2, insertedMovies.captured.size)
        assertEquals(1, insertedMovies.captured[0].id)
        assertEquals(2, insertedMovies.captured[1].id)
    }

    @Test
    fun `getMovieDetail returns cached data when available and not forced refresh`() = runBlocking {


        val movieId = 1
        val cachedMovie = createMovieDetailEntity(movieId)

        coEvery { movieDao.getMovieById(movieId) } returns createMovieEntity(movieId, "popular")
        every { movieDao.getMovieDetail(movieId) } returns flowOf(cachedMovie)


        val result = repository.getMovieDetail(movieId, forceRefresh = false).first()


        assertEquals(movieId, result?.id)
        coVerify(exactly = 0) { movieApi.getMovieDetail(movieId) }
    }

    @Test
    fun `getMovieDetail fetches from API when cache is empty`() = runBlocking {


        val movieId = 1
        val apiMovieDetail = createMovieDetailDto(movieId)

        coEvery { movieDao.getMovieById(movieId) } returns null
        coEvery { movieApi.getMovieDetail(movieId) } returns apiMovieDetail
        every { movieDao.getMovieDetail(movieId) } returns flowOf(null)

        val insertedMovieDetail = slot<MovieDetailEntity>()
        coEvery { movieDao.insertMovieDetail(capture(insertedMovieDetail)) } returns Unit


        repository.getMovieDetail(movieId, forceRefresh = false).first()



        coVerify { movieApi.getMovieDetail(movieId) }
        coVerify { movieDao.insertMovieDetail(any()) }
        assertEquals(movieId, insertedMovieDetail.captured.id)
    }


    private fun createMovieEntity(id: Int, category: String) = MovieEntity(
        id = id,
        title = "Movie $id",
        overview = "Overview $id",
        posterPath = "/poster$id.jpg",
        backdropPath = "/backdrop$id.jpg",
        voteAverage = 7.5,
        voteCount = 100,
        releaseDate = "2023-01-01",
        popularity = 500.0,
        category = category
    )

    private fun createMovieDto(id: Int) = MovieDto(
        id = id,
        title = "Movie $id",
        overview = "Overview $id",
        posterPath = "/poster$id.jpg",
        backdropPath = "/backdrop$id.jpg",
        voteAverage = 7.5,
        voteCount = 100,
        releaseDate = "2023-01-01",
        popularity = 500.0
    )

    private fun createMovieDetailEntity(id: Int) = MovieDetailEntity(
        id = id,
        title = "Movie $id",
        overview = "Overview $id",
        posterPath = "/poster$id.jpg",
        backdropPath = "/backdrop$id.jpg",
        voteAverage = 7.5,
        voteCount = 100,
        releaseDate = "2023-01-01",
        runtime = 120,
        genres = listOf(),
        budget = 1000000,
        revenue = 5000000,
        status = "Released",
        tagline = "Tagline $id",
        popularity = 500.0
    )

    private fun createMovieDetailDto(id: Int) = MovieDetailDto(
        id = id,
        title = "Movie $id",
        overview = "Overview $id",
        posterPath = "/poster$id.jpg",
        backdropPath = "/backdrop$id.jpg",
        voteAverage = 7.5,
        voteCount = 100,
        releaseDate = "2023-01-01",
        runtime = 120,
        genres = listOf(GenreDto(1, "Action"), GenreDto(2, "Drama")),
        budget = 1000000,
        revenue = 5000000,
        status = "Released",
        tagline = "Tagline $id",
        popularity = 500.0
    )
}