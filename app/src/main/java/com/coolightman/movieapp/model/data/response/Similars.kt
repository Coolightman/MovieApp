package com.coolightman.movieapp.model.data.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coolightman.movieapp.model.data.Movie
import com.google.gson.annotations.SerializedName

@Entity
data class Similars(
    var total: Int,
    @SerializedName("items") var movies: List<Movie>
){
    @PrimaryKey
    var movieId: Long = 0
}
