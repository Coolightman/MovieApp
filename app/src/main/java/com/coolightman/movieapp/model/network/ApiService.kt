package com.coolightman.movieapp.model.network

import com.coolightman.movieapp.model.data.*
import com.coolightman.movieapp.model.data.response.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    companion object {
        private const val HEADER_ACCEPT_NAME = "accept"
        private const val HEADER_ACCEPT_VALUE = "application/json"
        private const val HEADER_API_NAME = "X-API-KEY"
        private const val HEADER_API_VALUE = "655063ab-c3b3-40fd-86ce-7868c8fc3b57"
        private const val PARAM_TYPE = "type"
        private const val PARAM_PAGE = "page"

        private const val headerAccept = "$HEADER_ACCEPT_NAME: $HEADER_ACCEPT_VALUE"
        private const val headerKey = "$HEADER_API_NAME: $HEADER_API_VALUE"
    }

    @Headers(headerAccept, headerKey)
    @GET("v2.2/films/top")
    fun loadPageOfMovies(
        @Query(PARAM_TYPE) topType: String,
        @Query(PARAM_PAGE) page: Int
    ): Call<MoviesPage>

    @Headers(headerAccept, headerKey)
    @GET("v2.2/films/{id}")
    fun loadMovieDetails(@Path("id") movieId: Long): Call<MovieDetails>

    @Headers(headerAccept, headerKey)
    @GET("v2.1/films/{id}/frames")
    fun loadMovieFrames(@Path("id") movieId: Long): Call<Frames>

    @Headers(headerAccept, headerKey)
    @GET("v2.2/films/{id}/videos")
    fun loadMovieVideos(@Path("id") movieId: Long): Call<Videos>

    @Headers(headerAccept, headerKey)
    @GET("v2.2/films/{id}/facts")
    fun loadMovieFacts(@Path("id") movieId: Long): Call<Facts>

    @Headers(headerAccept, headerKey)
    @GET("v2.2/films/{id}/similars")
    fun loadSimilarMovies(@Path("id") movieId: Long): Call<Similars>
}