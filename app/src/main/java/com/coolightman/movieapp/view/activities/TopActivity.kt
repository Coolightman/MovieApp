package com.coolightman.movieapp.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.coolightman.movieapp.R

class TopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)

//        ApiClient.getService().loadMovieDetails(346).enqueue(object : Callback<MovieDetails> {
//            override fun onResponse(call: Call<MovieDetails>, response: Response<MovieDetails>) {
//                val details = response.raw().body
//            }
//
//            override fun onFailure(call: Call<MovieDetails>, t: Throwable) {
//
//            }
//        })
    }
}