package com.coolightman.movieapp.model.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.coolightman.movieapp.model.data.response.Videos

@Dao
interface VideosDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideos(vararg videos: Videos)

    @Query("select * from videos where movieId = :id")
    fun getVideosLiveData(id: Long): LiveData<Videos?>
}