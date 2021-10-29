package com.coolightman.movieapp.model.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import com.coolightman.movieapp.model.data.Fact
import com.coolightman.movieapp.model.data.Favorite
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.model.data.MovieDetails
import com.coolightman.movieapp.model.data.response.Facts
import com.coolightman.movieapp.model.data.response.Frames
import com.coolightman.movieapp.model.data.response.Similars
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
            if (movieDb != null) {
                this.movie = movieDb
                if (!movieDb.isDetailed) {
                    downloadMovieData(movieId)
                }
            } else {
                this.movie = Movie(movieId)
                downloadMovieData(movieId)
            }
        }
    }

    private fun downloadMovieData(movieId: Long) {
        downloadMovieDetails(movieId)
        downloadFrames(movieId)
        downloadVideos(movieId)
        downloadFacts(movieId)
        downloadSimilars(movieId)
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
            if (movie.rating == null){
                movie.rating = it.ratingKinopoisk
                movie.ratingCount = it.ratingKinopoiskVoteCount
            }
            if (movie.preview == null){
                movie.preview = it.posterUrlPreview
            }
            executor.execute { database.movieDao().insert(movie) }
        }
    }

    private fun downloadFrames(movieId: Long) {
        val framesCall = apiService.loadMovieFrames(movieId)
        framesCall.enqueue(object : Callback<Frames> {
            override fun onResponse(call: Call<Frames>, response: Response<Frames>) {
                if (response.isSuccessful) {
                    val frames = response.body()
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

    private fun downloadFacts(movieId: Long) {
        val factsCall = apiService.loadMovieFacts(movieId)
        factsCall.enqueue(object : Callback<Facts> {
            override fun onResponse(call: Call<Facts>, response: Response<Facts>) {
                if (response.isSuccessful) {
                    val facts = response.body()
                    facts?.let {
                        it.movieId = movieId
                        val cleanFacts = clearFacts(it)
                        it.items = cleanFacts
                        insertFactsInDb(it)
                    }
                } else {
                    Log.e("Response", "Response Facts is not successful")
                }
            }

            override fun onFailure(call: Call<Facts>, t: Throwable) {
                Log.e("Call", "Call Facts failure")
            }
        })
    }

    private fun clearFacts(facts: Facts): List<Fact> {
        val cleanFacts = mutableListOf<Fact>()
        for (fact in facts.items){
            val cleanText = HtmlCompat.fromHtml(fact.text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            fact.text = cleanText
            cleanFacts.add(fact)
        }
        return cleanFacts
    }

    private fun insertFactsInDb(facts: Facts) {
        executor.execute {
            database.factsDao().insertFacts(facts)
        }
    }

    private fun downloadSimilars(movieId: Long) {
        val similarsCall = apiService.loadSimilarMovies(movieId)
        similarsCall.enqueue(object : Callback<Similars> {
            override fun onResponse(call: Call<Similars>, response: Response<Similars>) {
                if (response.isSuccessful) {
                    val similars = response.body()
                    similars?.let {
                        it.movieId = movieId
                        insertSimilarsInDb(it)
                    }
                } else {
                    Log.e("Response", "Response Similars is not successful")
                }
            }

            override fun onFailure(call: Call<Similars>, t: Throwable) {
                Log.e("Call", "Call Similars failure")
            }
        })
    }

    private fun insertSimilarsInDb(similars: Similars) {
        executor.execute {
            database.similarsDao().insertSimilars(similars)
        }
    }

    fun getMovie(movieId: Long): LiveData<Movie> {
        return database.movieDao().getMovieLiveData(movieId)
    }

    fun getFrames(movieId: Long): LiveData<Frames> {
        return database.framesDao().getFramesLiveData(movieId)
    }

    fun getVideos(movieId: Long): LiveData<Videos> {
        return database.videosDao().getVideosLiveData(movieId)
    }

    fun getFacts(movieId: Long): LiveData<Facts> {
        return database.factsDao().getFactsLiveData(movieId)
    }

    fun getSimilars(movieId: Long): LiveData<Similars> {
        return database.similarsDao().getSimilarsLiveData(movieId)
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
        return Favorite(
            movie.movieId,
            movie.rating,
            movie.ratingCount,
            movie.preview,
            movie.isFavourite,
            movie.topPopularPlace,
            movie.top250Place,
            movie.topAwaitPlace,
            movie.isDetailed,
            movie.poster,
            movie.nameOriginal,
            movie.nameRu,
            movie.slogan,
            movie.year,
            movie.length,
            movie.genres,
            movie.countries,
            movie.description,
            movie.webUrl
        )
    }
}