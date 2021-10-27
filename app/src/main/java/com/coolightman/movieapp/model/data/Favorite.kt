package com.coolightman.movieapp.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Favorite(
    @PrimaryKey
    override val movieId: Long,
    override val rating: String? = null,
    override val ratingCount: String? = null,
    override val preview: String? = null,
    override var isFavourite: Boolean = false,
    override var topPopularPlace: Int = 0,
    override var top250Place: Int = 0,
    override var topAwaitPlace: Int = 0,
    override var isDetailed: Boolean = false,
    override var poster: String? = null,
    override var nameOriginal: String? = null,
    override var nameRu: String? = null,
    override var slogan: String? = null,
    override var year: String? = null,
    override var length: Int? = null,
    override var genres: List<Genre> = emptyList(),
    override var countries: List<Country> = emptyList(),
    override var description: String? = null,
    override var webUrl: String? = null
) : MovieDad

