package com.coolightman.movieapp.model.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.coolightman.movieapp.model.data.Movie

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(movies: List<Movie>)

    @Query("select * from movie where movieId = :id")
    fun getMovie(id: Long): Movie?

    @Query("select * from movie where topPopularPlace > 0 order by topPopularPlace")
    fun getAllPopular(): LiveData<List<Movie>>

    @Query("select * from movie where top250Place > 0 order by top250Place")
    fun getAll250(): LiveData<List<Movie>>

    @Query("select * from movie where topAwaitPlace > 0 order by topAwaitPlace")
    fun getAllAwait(): LiveData<List<Movie>>

    @Update
    fun update(vararg movie: Movie)

    @Query("delete from movie")
    fun clearTable()
}