package com.banquemisr.challenge05.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val releaseDate: String,
    val popularity: Double,
    val category: String, // "now_playing", "popular", or "upcoming"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "movie_details")
@TypeConverters(Converters::class)
data class MovieDetailEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val releaseDate: String,
    val runtime: Int?,
    val genres: List<GenreEntity>,
    val budget: Long,
    val revenue: Long,
    val status: String,
    val tagline: String?,
    val popularity: Double,
    val timestamp: Long = System.currentTimeMillis()
)

data class GenreEntity(
    val id: Int,
    val name: String
)