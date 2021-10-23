package com.coolightman.movieapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
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

}