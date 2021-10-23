package com.coolightman.movieapp.model.data.response

import com.coolightman.movieapp.model.data.Movie
import com.google.gson.annotations.SerializedName

class MoviesPage(
    var pagesCount: Int,
    @SerializedName("films") var movies: List<Movie>
)