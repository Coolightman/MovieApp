package com.coolightman.movieapp.model.db.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface FavoriteDao {

    @Query("select movieId from favorite")
    fun getFavoriteIds(): List<Long>
}