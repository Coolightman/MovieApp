package com.coolightman.movieapp.model.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.coolightman.movieapp.model.data.Movie

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg movie:Movie)

    @Query("select * from movie where movieId = :id")
    fun getMovie(id: Long): Movie?

    @Query("select * from movie where movieId = :id")
    fun getMovieLiveData(id: Long): LiveData<Movie>

    @Query("select * from movie where topPopularPlace > 0 order by topPopularPlace")
    fun getAllPopular(): LiveData<List<Movie>>

    @Query("select * from movie where topPopularPlace > 0")
    fun getAllPopularList(): List<Movie>

    @Query("select COUNT(movieId) from movie where topPopularPlace > 0")
    fun getAllPopularCount(): Int

    @Query("select * from movie where top250Place > 0 order by top250Place")
    fun getAll250(): LiveData<List<Movie>>

    @Query("select * from movie where top250Place > 0")
    fun getAll250List(): List<Movie>

    @Query("select COUNT(movieId) from movie where top250Place > 0")
    fun getAll250Count(): Int

    @Query("select * from movie where topAwaitPlace > 0 order by topAwaitPlace")
    fun getAllAwait(): LiveData<List<Movie>>

    @Query("select * from movie where topAwaitPlace > 0")
    fun getAllAwaitList(): List<Movie>

    @Query("select COUNT(movieId) from movie where topAwaitPlace > 0")
    fun getAllAwaitCount(): Int

    @Update
    fun update(vararg movie: Movie)

    @Query("delete from movie")
    fun clearTable()
}