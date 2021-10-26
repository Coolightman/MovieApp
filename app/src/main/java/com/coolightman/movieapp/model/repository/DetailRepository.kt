package com.coolightman.movieapp.model.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.model.data.MovieDetails
import com.coolightman.movieapp.model.data.response.Frames
import com.coolightman.movieapp.model.db.MovieDatabase
import com.coolightman.movieapp.model.network.ApiFactory
import com.coolightman.movieapp.util.ExecutorService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailRepository(private val application: Application) {

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
        val detailsCall = apiService.loadMovieDetails(movieId)
        detailsCall.enqueue(object : Callback<MovieDetails> {
            override fun onResponse(call: Call<MovieDetails>, response: Response<MovieDetails>) {
                if (response.isSuccessful) {
                    setMovieDetailsInMovie(response)
                } else {
                    Log.e("Response", "Response MovieDetails is not successful")
                }
            }

            override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
                Log.e("Call", "Call MovieDetails failure")
                Toast.makeText(application, "Internet is disconnected", Toast.LENGTH_SHORT).show()
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
            movie.webUrl = it.webUrl
            movie.isDetailed = true
            executor.execute { database.movieDao().update(movie) }
        }
    }

    fun loadFrames(movieId: Long) {
        executor.execute {
            downloadFrames(movieId)
        }
    }

    private fun downloadFrames(movieId: Long) {
        val framesCall = apiService.loadMovieFrames(movieId)
        framesCall.enqueue(object : Callback<Frames> {
            override fun onResponse(call: Call<Frames>, response: Response<Frames>) {
                if (response.isSuccessful) {
                    val frames = response.body()
                    Log.e("REsponseFrames", frames.toString())
                    frames?.let {
                        it.movieId = movieId
                        insertFramesInDb(it)
                    }
                } else {
                    Log.e("Response", "Response Frames is not successful")
                }
            }

            override fun onFailure(call: Call<Frames>, t: Throwable) {
                Log.e("Call", "Call Frames failure")
            }
        })
    }

    private fun insertFramesInDb(it: Frames) {
        executor.execute {
            database.framesDao().insertFrames(it)
        }
    }

    fun getMovie(movieId: Long): LiveData<Movie> {
        return database.movieDao().getMovieLiveData(movieId)
    }

    fun getFrames(movieId: Long): LiveData<Frames?> {
        return database.framesDao().getFramesLiveData(movieId)
    }


}