package com.coolightman.movieapp.model.data.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coolightman.movieapp.model.data.Frame

@Entity
data class Frames(
    @PrimaryKey
    var movieId: Long = 0,
    var frames: List<Frame>
)
