package com.coolightman.movieapp.model.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.model.data.response.MoviesPage
import com.coolightman.movieapp.model.db.MovieDatabase
import com.coolightman.movieapp.model.enums.Top
import com.coolightman.movieapp.model.network.ApiFactory
import com.coolightman.movieapp.util.ExecutorService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieRepository(private val application: Application) {

    private val apiService = ApiFactory.getService()
    private val database = MovieDatabase.getDb(application)
    private val executor = ExecutorService.getExecutor()
    private val handler = ExecutorService.getHandler()

    private var topPopularDownloadedPages = 0
    private var top250DownloadedPages = 0
    private var topAwaitDownloadedPages = 0

    private lateinit var topType: Top

    fun getMoviesTop(topType: Top): LiveData<List<Movie>> {
        this.topType = topType
        getDownloadedPagesCount()

        val list = when (topType) {
            Top.TOP_100_POPULAR_FILMS -> database.movieDao().getAllPopular()
            Top.TOP_250_BEST_FILMS -> database.movieDao().getAll250()
            Top.TOP_AWAIT_FILMS -> database.movieDao().getAllAwait()
        }
        return list
    }

    private fun getDownloadedPagesCount() {
        executor.execute {
            when (topType) {
                Top.TOP_100_POPULAR_FILMS -> {
                    val size = database.movieDao().getAllPopularCount()
                    topPopularDownloadedPages = size / 20
                    Log.e("sizePop", "$size")
                }
                Top.TOP_250_BEST_FILMS -> {
                    val size = database.movieDao().getAll250Count()
                    top250DownloadedPages = size / 20
                    Log.e("size250", "$size")
                }
                Top.TOP_AWAIT_FILMS -> {
                    val size = database.movieDao().getAllAwaitCount()
                    topAwaitDownloadedPages = size / 20
                    Log.e("sizeAwait", "$size")
                }
            }
            handler.post {
                loadFirstData()
            }
        }
    }

    private fun loadFirstData() {
        val downloadedPageNumber = getDownloadedPageNumber()
        if (downloadedPageNumber == 0) {
            loadNextPage()
        }
    }

    fun loadNextPage(): Boolean {
        val downloadedPage = getDownloadedPageNumber()
        Log.e("downloadedPage", "$downloadedPage")

        val totalPages = getTotalPages()
        val nextPage = downloadedPage + 1
        Log.e("nextPage", "$nextPage<=$totalPages")

        return if (nextPage <= totalPages) {
            Log.e("LoadNextPage", "TRIGGERED")
            loadPageOfMovies(topType, nextPage)
            true
        } else {
            false
        }
    }

    private fun resetDownloadedPage(page: Int) {
        when (topType) {
            Top.TOP_100_POPULAR_FILMS -> topPopularDownloadedPages = page
            Top.TOP_250_BEST_FILMS -> top250DownloadedPages = page
            Top.TOP_AWAIT_FILMS -> topAwaitDownloadedPages = page
        }
    }

    private fun loadPageOfMovies(topType: Top, page: Int) {
        apiService.loadPageOfMovies(topType.name, page).enqueue(object : Callback<MoviesPage> {
            override fun onResponse(call: Call<MoviesPage>, response: Response<MoviesPage>) {
                if (response.isSuccessful) {
                    Log.i("Response", response.toString())
                    setMoviesPageInDb(response, page)
                } else {
                    Log.e("Response", "Response MoviesPage is not successful")
                }
            }

            override fun onFailure(call: Call<MoviesPage>, t: Throwable) {
                Log.e("Call", "Call failure")
                Toast.makeText(application, "Internet is disconnected", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setMoviesPageInDb(response: Response<MoviesPage>, page: Int) {
        val list = response.body()?.movies
        list?.let {
            executor.execute {
                val updatedList = setAdditionalFields(it, page)
                database.movieDao().insertList(updatedList)
                resetDownloadedPage(page)
            }
        }
    }

    private fun setAdditionalFields(movies: List<Movie>, page: Int): List<Movie> {
        val updatedFavList = setFavoritesFromDb(movies)
        val updatedTopNumberList = setTopNumbers(updatedFavList, page)
        val updatedList = setAnotherTopNumbers(updatedTopNumberList)
        return updatedList
    }

    private fun setAnotherTopNumbers(movies: List<Movie>): List<Movie> {
        val updatedList = mutableListOf<Movie>()
        for (movie in movies) {
            val movieDb = database.movieDao().getMovie(movie.movieId)
            if (movieDb != null) {
                when (topType) {
                    Top.TOP_100_POPULAR_FILMS -> {
                        movieDb.topPopularPlace = movie.topPopularPlace
                    }
                    Top.TOP_250_BEST_FILMS -> {
                        movieDb.top250Place = movie.top250Place
                    }
                    Top.TOP_AWAIT_FILMS -> {
                        movieDb.topAwaitPlace = movie.topAwaitPlace
                    }
                }
                updatedList.add(movieDb)
            } else {
                updatedList.add(movie)
            }
        }
        return updatedList
    }

    private fun setTopNumbers(movies: List<Movie>, page: Int): List<Movie> {
        val updatedList = mutableListOf<Movie>()
        when (topType) {
            Top.TOP_100_POPULAR_FILMS -> {
                for (movie in movies) {
                    movie.topPopularPlace = movies.indexOf(movie) + 1 + (page - 1) * 20
                    updatedList.add(movie)
                }
                return updatedList
            }
            Top.TOP_250_BEST_FILMS -> {
                for (movie in movies) {
                    movie.top250Place = movies.indexOf(movie) + 1 + (page - 1) * 20
                    updatedList.add(movie)
                }
                return updatedList
            }
            Top.TOP_AWAIT_FILMS -> {
                for (movie in movies) {
                    movie.topAwaitPlace = movies.indexOf(movie) + 1 + (page - 1) * 20
                    updatedList.add(movie)
                }
                return updatedList
            }
        }
    }

    private fun setFavoritesFromDb(movies: List<Movie>): List<Movie> {
        val updatedList = mutableListOf<Movie>()
        val favoriteIds = database.favoriteDao().getFavoriteIds()
        for (movie in movies) {
            val favoriteIndex = favoriteIds.indexOf(movie.movieId)
            if (favoriteIndex != -1) {
                movie.isFavourite = true
            }
            updatedList.add(movie)
        }
        return updatedList
    }

    private fun getTotalPages(): Int {
        return when (topType) {
            Top.TOP_100_POPULAR_FILMS -> 5
            Top.TOP_250_BEST_FILMS -> 13
            Top.TOP_AWAIT_FILMS -> 1
        }
    }

    private fun getDownloadedPageNumber(): Int {
        return when (topType) {
            Top.TOP_100_POPULAR_FILMS -> topPopularDownloadedPages
            Top.TOP_250_BEST_FILMS -> top250DownloadedPages
            Top.TOP_AWAIT_FILMS -> topAwaitDownloadedPages
        }
    }

    fun refreshData() {
        when (topType) {
            Top.TOP_100_POPULAR_FILMS -> executor.execute {
                clearPopular()
                handler.post { getMoviesTop(topType) }
            }
            Top.TOP_250_BEST_FILMS -> executor.execute {
                clear250()
                handler.post { getMoviesTop(topType) }
            }
            Top.TOP_AWAIT_FILMS -> executor.execute {
                clearAwait()
                handler.post { getMoviesTop(topType) }
            }
        }
    }

    private fun clearAwait() {
        val movies = database.movieDao().getAllAwaitList()
        for (movie in movies) {
            movie.topAwaitPlace = 0
        }
        database.movieDao().insertList(movies)
    }

    private fun clear250() {
        val movies = database.movieDao().getAll250List()
        for (movie in movies) {
            movie.top250Place = 0
        }
        database.movieDao().insertList(movies)
    }

    private fun clearPopular() {
        val movies = database.movieDao().getAllPopularList()
        for (movie in movies) {
            movie.topPopularPlace = 0
        }
        database.movieDao().insertList(movies)
    }
}