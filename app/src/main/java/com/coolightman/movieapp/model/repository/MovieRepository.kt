package com.coolightman.movieapp.model.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.model.db.MovieDatabase
import com.coolightman.movieapp.model.enums.Top
import com.coolightman.movieapp.model.network.ApiFactory

class MovieRepository(application: Application) {

    private val apiService = ApiFactory.getService()
    private val database = MovieDatabase.getDb(application)

//    fun getMoviesPage(topType: Top, page: Int): LiveData<List<Movie>> {
//
//    }
}