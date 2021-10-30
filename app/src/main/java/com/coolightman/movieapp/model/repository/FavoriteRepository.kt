package com.coolightman.movieapp.model.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.coolightman.movieapp.model.data.Favorite
import com.coolightman.movieapp.model.db.MovieDatabase

class FavoriteRepository(application: Application) {

    private val database = MovieDatabase.getDb(application)

    fun getFavorite(): LiveData<List<Favorite>>{
        return database.favoriteDao().getALlLiveData()
    }
}