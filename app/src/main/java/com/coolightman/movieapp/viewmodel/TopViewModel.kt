package com.coolightman.movieapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.coolightman.movieapp.model.repository.MovieRepository

class TopViewModel(application: Application) : AndroidViewModel(application) {
    private val movieRepository = MovieRepository(application)


}