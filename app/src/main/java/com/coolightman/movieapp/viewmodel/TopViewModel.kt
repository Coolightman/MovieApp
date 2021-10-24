package com.coolightman.movieapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.model.enums.Top
import com.coolightman.movieapp.model.repository.MovieRepository
import com.coolightman.movieapp.util.ExecutorService
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class TopViewModel(application: Application) : AndroidViewModel(application) {
    private val movieRepository = MovieRepository(application)

    private var topType = Top.TOP_100_POPULAR_FILMS
    private var topTypeTrigger = MutableLiveData<Top>()

    fun setTopType(topType: Top) {
        this.topType = topType
        this.topTypeTrigger.value = topType
    }

    fun getTopMovies(): LiveData<List<Movie>> {
        return Transformations.switchMap(topTypeTrigger) {
            movieRepository.getMoviesTop(topType)
        }
    }

    fun loadNextPage() {
        val isPageLoaded = movieRepository.loadNextPage()
        if (!isPageLoaded){
            this.topTypeTrigger.value = topType
        }
    }

    fun refreshData() {
        movieRepository.refreshData()
    }
}