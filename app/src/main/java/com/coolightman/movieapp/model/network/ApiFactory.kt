package com.coolightman.movieapp.model.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {
    private var retrofit: Retrofit? = null
    private const val SWAGGER_API_URL = "https://kinopoiskapiunofficial.tech/api/"

    private fun getClient(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(SWAGGER_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun getService(): ApiService {
        return getClient().create(ApiService::class.java)
    }
}