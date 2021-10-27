package com.coolightman.movieapp.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coolightman.movieapp.model.data.Favorite
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.model.data.response.Facts
import com.coolightman.movieapp.model.data.response.Frames
import com.coolightman.movieapp.model.data.response.Similars
import com.coolightman.movieapp.model.data.response.Videos
import com.coolightman.movieapp.model.db.dao.*

@Database(
    version = 10,
    entities = [
        Movie::class, Frames::class, Videos::class,
        Facts::class, Similars::class, Favorite::class
    ]
)
@TypeConverters(Converters::class)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun framesDao(): FramesDao
    abstract fun videosDao(): VideosDao
    abstract fun factsDao(): FactsDao
    abstract fun similarsDao(): SimilarsDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        private var INSTANCE: MovieDatabase? = null
        private const val DB_NAME = "movieApp.db"

        fun getDb(context: Context): MovieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    DB_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}