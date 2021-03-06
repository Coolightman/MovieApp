package com.coolightman.movieapp.model.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.model.data.response.MoviesPage
import com.coolightman.movieapp.model.db.MovieDatabase
import com.coolightman.movieapp.model.enums.Top
import com.coolightman.movieapp.model.network.ApiFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieRepository(private val application: Application) {

    private val apiService = ApiFactory.getService()
    private val database = MovieDatabase.getDb(application)

    private val parentJob = Job()
    private val coroutineContext = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    private var topPopularDownloadedPages = 0
    private var top250DownloadedPages = 0
    private var topAwaitDownloadedPages = 0

    private var isAllTopDownloaded = MutableLiveData<Boolean>()

    private lateinit var topType: Top

    companion object {
        private const val TOP_POPULAR_TOTAL_PAGES = 5
        private const val TOP_250_TOTAL_PAGES = 13
        private const val TOP_AWAIT_TOTAL_PAGES = 1
    }

    fun getTop(topType: Top): LiveData<List<Movie>> {
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
        scope.launch {
            when (topType) {
                Top.TOP_100_POPULAR_FILMS -> {
                    val size = database.movieDao().getAllPopularCount()
                    topPopularDownloadedPages = size / 20
                }
                Top.TOP_250_BEST_FILMS -> {
                    val size = database.movieDao().getAll250Count()
                    top250DownloadedPages = size / 20
                }
                Top.TOP_AWAIT_FILMS -> {
                    val size = database.movieDao().getAllAwaitCount()
                    topAwaitDownloadedPages = size / 20
                }
            }
            loadTopFirstData()
        }
    }

    private fun loadTopFirstData() {
        val downloadedPageNumber = getDownloadedPageNumber()
        if (downloadedPageNumber == 0) {
            loadNextPage()
        }
    }

    fun loadNextPage() {
        val downloadedPage = getDownloadedPageNumber()
        val totalPages = getTotalPages()

        if (downloadedPage < totalPages) {
            loadPageOfMovies(topType, downloadedPage + 1)
        } else {
            isAllTopDownloaded.value = true
        }
    }

    private fun loadPageOfMovies(topType: Top, page: Int) {
        apiService.loadPageOfMovies(topType.name, page).enqueue(object : Callback<MoviesPage> {
            override fun onResponse(call: Call<MoviesPage>, response: Response<MoviesPage>) {
                if (response.isSuccessful) {
                    setMoviesPageInDb(response, page)
                } else {
                    Log.e("Response", "Response MoviesPage is not successful")
                }
            }

            override fun onFailure(call: Call<MoviesPage>, t: Throwable) {
                Log.e("Call", "Call MoviesPage failure")
                Toast.makeText(application, "Internet is disconnected", Toast.LENGTH_SHORT).show()
                isAllTopDownloaded.value = true
            }
        })
    }

    private fun setMoviesPageInDb(response: Response<MoviesPage>, page: Int) {
        val list = response.body()?.movies
        list?.let {
            scope.launch {
                val updatedList = setAdditionalFields(it, page)
                database.movieDao().insertList(updatedList)
                resetDownloadedPage(page)
            }
        }
    }

    private fun resetDownloadedPage(page: Int) {
        when (topType) {
            Top.TOP_100_POPULAR_FILMS -> topPopularDownloadedPages = page
            Top.TOP_250_BEST_FILMS -> top250DownloadedPages = page
            Top.TOP_AWAIT_FILMS -> topAwaitDownloadedPages = page
        }
    }

    private fun setAdditionalFields(movies: List<Movie>, page: Int): List<Movie> {
        val updatedFavList = setFavoritesFromDb(movies)
        val updatedTopNumberList = setTopNumbers(updatedFavList, page)
        val updatedList = setAnotherTopNumbers(updatedTopNumberList)
        Log.i("updatedList", updatedList.toString())
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
            Top.TOP_100_POPULAR_FILMS -> TOP_POPULAR_TOTAL_PAGES
            Top.TOP_250_BEST_FILMS -> TOP_250_TOTAL_PAGES
            Top.TOP_AWAIT_FILMS -> TOP_AWAIT_TOTAL_PAGES
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
        scope.launch {
            when (topType) {
                Top.TOP_100_POPULAR_FILMS -> {
                    clearPopular()
                    getTop(topType)
                }
                Top.TOP_250_BEST_FILMS -> {
                    clear250()
                    getTop(topType)
                }
                Top.TOP_AWAIT_FILMS -> {
                    clearAwait()
                    getTop(topType)
                }
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

    fun getIsAllTopDownloaded(): MutableLiveData<Boolean> {
        return isAllTopDownloaded
    }
}