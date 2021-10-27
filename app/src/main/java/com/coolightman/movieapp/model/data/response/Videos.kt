package com.coolightman.movieapp.model.data.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coolightman.movieapp.model.data.Video

@Entity
data class Videos(
    @PrimaryKey
    var movieId: Long = 0,
    var total: Int,
    var items: List<Video>
)
