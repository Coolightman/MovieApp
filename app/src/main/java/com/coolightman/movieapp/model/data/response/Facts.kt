package com.coolightman.movieapp.model.data.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coolightman.movieapp.model.data.Fact

@Entity
data class Facts(
    @PrimaryKey
    var movieId: Long = 0,
    var total: Int,
    var items: List<Fact>
)
