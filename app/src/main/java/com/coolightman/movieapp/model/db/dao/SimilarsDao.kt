package com.coolightman.movieapp.model.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.coolightman.movieapp.model.data.response.Similars

@Dao
interface SimilarsDao {

    @Insert
    fun insertSimilars(vararg similars: Similars)

    @Query("select * from similars where movieId = :id")
    fun getSimilarsLiveData(id: Long): LiveData<Similars?>
}