package com.banquemisr.challenge05.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(
    entities = [MovieEntity::class, MovieDetailEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}

class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromGenreList(genres: List<GenreEntity>): String {
        return gson.toJson(genres)
    }
    
    @TypeConverter
    fun toGenreList(genresString: String): List<GenreEntity> {
        val type = object : TypeToken<List<GenreEntity>>() {}.type
        return gson.fromJson(genresString, type)
    }
}