package com.coolightman.movieapp.model.data

interface MovieDad {
    val movieId: Long
    val rating: String?
    val ratingCount: String?
    val preview: String?
    var isFavourite: Boolean
    var topPopularPlace: Int
    var top250Place: Int
    var topAwaitPlace: Int
    var isDetailed: Boolean
    var poster: String?
    var nameOriginal: String?
    var nameRu: String?
    var slogan: String?
    var year: String?
    var length: Int?
    var genres: List<Genre>
    var countries: List<Country>
    var description: String?
    var webUrl: String?
}