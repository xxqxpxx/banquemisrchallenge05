package com.banquemisr.challenge05.di

import com.banquemisr.challenge05.data.local.MovieDao
import com.banquemisr.challenge05.data.remote.Api
import com.banquemisr.challenge05.data.repo.MovieRepositoryImpl
import com.banquemisr.challenge05.domain.repo.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMovieRepository(
        api: Api,
        dao: MovieDao
    ): MovieRepository {
        return MovieRepositoryImpl(api, dao)
    }
}