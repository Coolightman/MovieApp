package com.coolightman.movieapp.model.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.coolightman.movieapp.model.data.response.Facts

@Dao
interface FactsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFacts(vararg facts: Facts)

    @Query("select * from facts where movieId = :id")
    fun getFactsLiveData(id: Long): LiveData<Facts>
}