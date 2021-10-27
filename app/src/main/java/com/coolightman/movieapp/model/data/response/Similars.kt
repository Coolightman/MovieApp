package com.coolightman.movieapp.model.data.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coolightman.movieapp.model.data.Movie

@Entity
data class Similars(
    @PrimaryKey
    var movieId: Long = 0,
    var total: Int,
    var items: List<Movie>
)
