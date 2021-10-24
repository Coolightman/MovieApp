package com.coolightman.movieapp.model.repository

import android.app.Application
import android.util.Log
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

class MovieRepository(application: Application) {

    private val apiService = ApiFactory.getService()
    private val database = MovieDatabase.getDb(application)
    private val executor = ExecutorService.getExecutor()

    private var topPopularDownloadedPages = 0
    private var top250DownloadedPages = 0
    private var topAwaitDownloadedPages = 0

    private var topPopularTotalPages = 1
    private var top250TotalPages = 1
    private var topAwaitTotalPages = 1

    private var nextPage = 0

    private lateinit var topType: Top

    fun getMoviesTop(topType: Top): LiveData<List<Movie>> {
        this.topType = topType
        loadFirstPage()

        val list = when (topType) {
            Top.TOP_100_POPULAR_FILMS -> database.movieDao().getAllPopular()
            Top.TOP_250_BEST_FILMS -> database.movieDao().getAll250()
            Top.TOP_AWAIT_FILMS -> database.movieDao().getAllAwait()
        }
        return list
    }

    private fun loadFirstPage() {
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
        if (nextPage <= totalPages) {
            Log.e("LoadNextPage", "TRIGGERED")
            this.nextPage = nextPage
            loadPageOfMovies(topType, nextPage)
            return true
        } else {
            return false
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
            }
        })
    }

    private fun setMoviesPageInDb(response: Response<MoviesPage>, page: Int) {
        val list = response.body()?.movies
        setTotalPages(response)
        list?.let {
            executor.execute {
                val updatedList = setAdditionalFields(it, page)
                database.movieDao().insertList(updatedList)
                resetDownloadedPage(nextPage)
            }
        }
    }

    private fun setTotalPages(response: Response<MoviesPage>) {
        when (topType) {
            Top.TOP_100_POPULAR_FILMS -> {
                if (topPopularTotalPages == 1) {
                    topPopularTotalPages = response.body()?.pagesCount!!
                }
            }
            Top.TOP_250_BEST_FILMS -> {
                if (top250TotalPages == 1) {
                    top250TotalPages = response.body()?.pagesCount!!
                }
            }
            Top.TOP_AWAIT_FILMS -> {
                if (topAwaitTotalPages == 1) {
                    topAwaitTotalPages = response.body()?.pagesCount!!
                }
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
            Top.TOP_100_POPULAR_FILMS -> topPopularTotalPages
            Top.TOP_250_BEST_FILMS -> top250TotalPages
            Top.TOP_AWAIT_FILMS -> topAwaitTotalPages
        }
    }

    private fun getDownloadedPageNumber(): Int {
        return when (topType) {
            Top.TOP_100_POPULAR_FILMS -> topPopularDownloadedPages
            Top.TOP_250_BEST_FILMS -> top250DownloadedPages
            Top.TOP_AWAIT_FILMS -> topAwaitDownloadedPages
        }
    }

    fun clearMovieDb() {
        database.movieDao().clearTable()
    }
}