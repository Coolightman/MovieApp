package com.coolightman.movieapp.model.data

import com.google.gson.annotations.SerializedName

data class MovieDetails(
    @SerializedName("posterUrl")
    val poster: String?,
    val nameOriginal: String?,
    val nameRu: String?,
    val slogan: String?,
    val year: String?,
    @SerializedName("filmLength")
    val length: Int?,
    val genres: List<Genre>?,
    val countries: List<Country>?,
    val description: String?
)
