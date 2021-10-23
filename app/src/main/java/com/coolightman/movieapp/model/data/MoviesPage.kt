package com.coolightman.movieapp.model.data

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity
class MoviesPage (
    var pagesCount : Int,
    @SerializedName("films") var movies : List<Movie>
)