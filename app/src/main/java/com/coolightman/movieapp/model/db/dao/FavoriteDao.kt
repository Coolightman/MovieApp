package com.coolightman.movieapp.model.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.coolightman.movieapp.model.data.Favorite
import com.coolightman.movieapp.model.data.MovieDad

@Dao
interface FavoriteDao {

    @Query("select movieId from favorite")
    fun getFavoriteIds(): List<Long>

    @Insert
    fun insertFavorite(vararg favorite: Favorite)

    @Query("delete from favorite where movieId = :id")
    fun deleteFavorite(id: Long)
}