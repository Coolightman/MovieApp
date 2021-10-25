package com.coolightman.movieapp.model.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.model.data.MovieDetails
import com.coolightman.movieapp.model.db.MovieDatabase
import com.coolightman.movieapp.model.network.ApiFactory
import com.coolightman.movieapp.util.ExecutorService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailRepository(application: Application) {

    private val apiService = ApiFactory.getService()
    private val database = MovieDatabase.getDb(application)
    private val executor = ExecutorService.getExecutor()
    private val handler = ExecutorService.getHandler()

    private lateinit var movie: Movie

    fun loadMovieData(movieId: Long) {
        executor.execute {
            val movieDb = database.movieDao().getMovie(movieId)
            movieDb?.let {
                this.movie = it
                if (!movie.isDetailed) {
                    downloadMovieDetails(it.movieId)
                }
            }
        }
    }

    private fun downloadMovieDetails(movieId: Long) {
        val details = apiService.loadMovieDetails(movieId)
        details.enqueue(object : Callback<MovieDetails> {
            override fun onResponse(call: Call<MovieDetails>, response: Response<MovieDetails>) {
                if (response.isSuccessful) {
                    setMovieDetailsInMovie(response)
                } else {
                    Log.e("Response", "Response MovieDetails is not successful")
                }
            }

            override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
                Log.e("Call", "Call MovieDetails failure")
            }
        })
    }

    private fun setMovieDetailsInMovie(response: Response<MovieDetails>) {
        val details = response.body()
        details?.let {
            movie.poster = it.poster
            movie.nameOriginal = it.nameOriginal
            movie.nameRu = it.nameRu
            movie.slogan = it.slogan
            movie.year = it.year
            movie.length = it.length
            movie.genres = it.genres
            movie.countries = it.countries
            movie.description = it.description
            movie.isDetailed = true
            executor.execute { database.movieDao().update(movie) }
        }
    }

    fun getMovie(movieId: Long): LiveData<Movie> {
        return database.movieDao().getMovieLiveData(movieId)
    }
}