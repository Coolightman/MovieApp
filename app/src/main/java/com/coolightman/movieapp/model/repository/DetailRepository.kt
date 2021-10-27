package com.coolightman.movieapp.model.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.coolightman.movieapp.model.data.Favorite
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.model.data.MovieDetails
import com.coolightman.movieapp.model.data.response.Frames
import com.coolightman.movieapp.model.data.response.Videos
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

    private lateinit var movie: Movie

    fun loadMovieData(movieId: Long) {
        executor.execute {
            val movieDb = database.movieDao().getMovie(movieId)
            movieDb?.let {
                this.movie = it
                if (!it.isDetailed) {
                    downloadMovieDetails(movieId)
                    downloadFrames(movieId)
                    downloadVideos(movieId)
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

    private fun downloadFrames(movieId: Long) {
        val framesCall = apiService.loadMovieFrames(movieId)
        framesCall.enqueue(object : Callback<Frames> {
            override fun onResponse(call: Call<Frames>, response: Response<Frames>) {
                if (response.isSuccessful) {
                    val frames = response.body()
                    Log.e("framesBody", frames.toString())
                    frames?.let {
                        it.movieId = movieId
                        Log.e("responseFrames", frames.toString())
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

    private fun downloadVideos(movieId: Long) {
        val videosCall = apiService.loadMovieVideos(movieId)
        videosCall.enqueue(object : Callback<Videos> {
            override fun onResponse(call: Call<Videos>, response: Response<Videos>) {
                if (response.isSuccessful) {
                    val videos = response.body()
                    videos?.let {
                        it.movieId = movieId
                        insertVideosInDb(videos)
                    }
                } else {
                    Log.e("Response", "Response Videos is not successful")
                }
            }

            override fun onFailure(call: Call<Videos>, t: Throwable) {
                Log.e("Call", "Call Videos failure")
            }
        })
    }

    private fun insertVideosInDb(videos: Videos) {
        executor.execute {
            database.videosDao().insertVideos(videos)
        }
    }

    fun getMovie(movieId: Long): LiveData<Movie> {
        return database.movieDao().getMovieLiveData(movieId)
    }

    fun getFrames(movieId: Long): LiveData<Frames?> {
        return database.framesDao().getFramesLiveData(movieId)
    }

    fun getVideos(movieId: Long): LiveData<Videos?> {
        return database.videosDao().getVideosLiveData(movieId)
    }

    fun updateMovieInDb(movie: Movie) {
        executor.execute {
            val favorite = getFavoriteFromMovie(movie)
            if (movie.isFavourite) {
                insertFavorite(favorite)
            } else {
                deleteFavorite(favorite)
            }
            database.movieDao().update(movie)
        }
    }

    private fun deleteFavorite(favorite: Favorite) {
        database.favoriteDao().deleteFavorite(favorite.movieId)
    }

    private fun insertFavorite(favorite: Favorite) {
        database.favoriteDao().insertFavorite(favorite)
    }

    private fun getFavoriteFromMovie(movie: Movie): Favorite {
        val favorite = Favorite(movie.movieId, movie.rating, movie.ratingCount, movie.preview)
        favorite.isFavourite = movie.isFavourite
        favorite.topPopularPlace = movie.topPopularPlace
        favorite.top250Place = movie.top250Place
        favorite.topAwaitPlace = movie.topAwaitPlace
        favorite.isDetailed = movie.isDetailed
        favorite.poster = movie.poster
        favorite.nameOriginal = movie.nameOriginal
        favorite.nameRu = movie.nameRu
        favorite.slogan = movie.slogan
        favorite.year = movie.year
        favorite.length = movie.length
        favorite.genres = movie.genres
        favorite.countries = movie.countries
        favorite.description = movie.description
        favorite.webUrl = movie.webUrl
        return favorite
    }
}