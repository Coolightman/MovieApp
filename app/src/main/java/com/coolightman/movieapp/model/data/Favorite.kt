package com.coolightman.movieapp.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Favorite(
    @PrimaryKey
    val movieId: Long,
    val rating: String? = null,
    val ratingCount: String? = null,
    val preview: String? = null
) {
    var isFavourite: Boolean = false
    var placeTop100: String? = null
    var placeTop250: String? = null
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
