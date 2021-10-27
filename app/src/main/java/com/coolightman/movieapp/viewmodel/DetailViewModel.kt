package com.coolightman.movieapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.model.data.response.Frames
import com.coolightman.movieapp.model.data.response.Videos
import com.coolightman.movieapp.model.repository.DetailRepository

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val detailRepository = DetailRepository(application)

    fun loadMovieData(movieId: Long) {
        detailRepository.loadMovieData(movieId)
    }

    fun getMovie(movieId: Long): LiveData<Movie> {
        return detailRepository.getMovie(movieId)
    }

    fun getFrames(movieId: Long): LiveData<Frames?> {
        return detailRepository.getFrames(movieId)
    }

    fun getVideos(movieId: Long): LiveData<Videos?> {
        return detailRepository.getVideos(movieId)
    }

    fun updateMovieInDb(movie: Movie) {
        detailRepository.updateMovieInDb(movie)
    }


//    fun getFacts(): LiveData<List<Fact>> {
//        TODO("Not yet implemented")
//    }
//
//    fun getSimilars(): LiveData<List<Movie>> {
//        TODO("Not yet implemented")
//    }
}