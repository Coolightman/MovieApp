package com.coolightman.movieapp.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Videos(
    var total: Int,
    var items: List<Video>
){
    @PrimaryKey
    var movieId: Long = 0
}
