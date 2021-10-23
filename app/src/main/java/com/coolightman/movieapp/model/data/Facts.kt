package com.coolightman.movieapp.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Facts(
    var total : Int,
    var items : List<Fact>
){
    @PrimaryKey
    var movieId: Long = 0
}
