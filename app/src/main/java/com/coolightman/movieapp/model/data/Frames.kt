package com.coolightman.movieapp.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Frames(
    var frames: List<Frame>
) {
    @PrimaryKey
    var movieId: Long = 0
}
