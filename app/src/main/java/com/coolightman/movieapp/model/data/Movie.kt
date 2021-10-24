package com.coolightman.movieapp.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Movie(
    @PrimaryKey
    @SerializedName("filmId")
    val movieId: Long,
    val rating: String? = null,
    @SerializedName("ratingVoteCount")
    val ratingCount: String? = null,
    @SerializedName("posterUrlPreview")
    val preview: String? = null
){
    var isFavourite: Boolean = false
    var topPopularPlace: Int = 0
    var top250Place: Int = 0
    var topAwaitPlace: Int = 0
    var isDetailed: Boolean = false
    var poster: String? = null
    var nameOriginal: String? = null
    var nameRu: String? = null
    var slogan: String? = null
    var year: String? = null
    var length: Int? = null
    var genres: List<Genre>? = null
    var countries: List<Country>? = null
    var description: String? = null
}
