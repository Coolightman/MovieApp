package com.coolightman.movieapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.coolightman.movieapp.model.data.Favorite
import com.coolightman.movieapp.model.repository.FavoriteRepository

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {
    private val favoriteRepository = FavoriteRepository(application)

    fun getFavorite():LiveData<List<Favorite>>{
        return favoriteRepository.getFavorite()
    }
}