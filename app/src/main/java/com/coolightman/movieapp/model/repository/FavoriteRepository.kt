package com.coolightman.movieapp.model.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.coolightman.movieapp.model.data.Favorite
import com.coolightman.movieapp.model.db.MovieDatabase
import com.coolightman.movieapp.model.network.ApiFactory
import com.coolightman.movieapp.util.ExecutorService

class FavoriteRepository(application: Application) {

    private val apiService = ApiFactory.getService()
    private val database = MovieDatabase.getDb(application)
    private val executor = ExecutorService.getExecutor()

    fun getFavorite(): LiveData<List<Favorite>>{
        return database.favoriteDao().getALlLiveData()
    }
}