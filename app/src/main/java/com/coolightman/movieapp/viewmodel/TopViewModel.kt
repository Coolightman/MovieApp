package com.coolightman.movieapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.model.enums.Top
import com.coolightman.movieapp.model.repository.MovieRepository

class TopViewModel(application: Application) : AndroidViewModel(application) {
    private val movieRepository = MovieRepository(application)

    private var topType = Top.TOP_100_POPULAR_FILMS
    private var topTypeTrigger = MutableLiveData<Top>()

    fun setTopType(topType: Top) {
        this.topType = topType
        this.topTypeTrigger.value = topType
    }

    fun getLiveDataTop(): LiveData<List<Movie>> {
        return Transformations.switchMap(topTypeTrigger) {
            movieRepository.getTop(topType)
        }
    }

    fun loadNextPage() {
        movieRepository.loadNextPage()
    }

    fun refreshData() {
        movieRepository.refreshData()
    }

    fun getIsAllTopDownloaded(): MutableLiveData<Boolean> {
        return movieRepository.getIsAllTopDownloaded()
    }
}