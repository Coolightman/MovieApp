package com.coolightman.movieapp.model.data.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coolightman.movieapp.model.data.Frame

@Entity
data class Frames(
    var frames: List<Frame>
) {
    @PrimaryKey
    var movieId: Long = 0
}
