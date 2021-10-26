package com.coolightman.movieapp.model.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.coolightman.movieapp.model.data.response.Frames

@Dao
interface FramesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFrames(vararg frames: Frames)

    @Query("select * from frames where movieId = :id ")
    fun getFramesLiveData(id: Long): LiveData<Frames?>

    @Query("select * from frames where movieId = :id ")
    fun getFrames(id: Long): Frames?
}