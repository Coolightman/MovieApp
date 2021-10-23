package com.coolightman.movieapp.model.data

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity
data class Similars(
    var total: Int,
    @SerializedName("items") var movies: List<Movie>
)
