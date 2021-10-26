package com.coolightman.movieapp.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.coolightman.movieapp.R
import com.coolightman.movieapp.model.data.Country
import com.coolightman.movieapp.model.data.Genre
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.model.data.Video
import com.coolightman.movieapp.view.adapters.FrameAdapter
import com.coolightman.movieapp.view.adapters.VideoAdapter
import com.coolightman.movieapp.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.movie_info.*

class DetailActivity : AppCompatActivity() {

    private lateinit var detailViewModel: DetailViewModel
    private lateinit var movie: Movie
    private lateinit var frameAdapter: FrameAdapter
    private lateinit var videoAdapter: VideoAdapter

    companion object {
        const val POSTER_HEIGHT = 1100
        const val POSTER_WIDTH = 740
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        detailViewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        val movieId = intent.getLongExtra("id", -1)

        createObservingData(movieId)
        createAdapters()
        listeners()
        detailViewModel.loadMovieData(movieId)
    }

    private fun listeners() {
        imageViewStar.setOnClickListener {
            if (movie.isFavourite) {
                movie.isFavourite = false
                detailViewModel.updateMovieInDb(movie)
                shortToast(getString(R.string.favorite_added))
            } else {
                movie.isFavourite = true
                detailViewModel.updateMovieInDb(movie)
                shortToast(getString(R.string.favorite_deleted))
            }
        }

        textViewKinopoisk.setOnClickListener {
            movie.webUrl?.let {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(movie.webUrl))
                startActivity(intent)
            }
        }
    }

    private fun shortToast(text: String) {
        val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast.show()
        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        }, 700)
    }

    private fun createAdapters() {
        createFrameAdapter()
        createVideoAdapter()
    }

    private fun createVideoAdapter() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewVideos.layoutManager = layoutManager
        videoAdapter = VideoAdapter { onVideoItemClickListener(it) }
        recyclerViewVideos.adapter = videoAdapter
    }

    private fun onVideoItemClickListener(video: Video) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.url))
        startActivity(intent)
    }

    private fun createFrameAdapter() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewFrames.layoutManager = layoutManager
        frameAdapter = FrameAdapter { onFrameItemClickListener() }
        recyclerViewFrames.adapter = frameAdapter
    }

    private fun onFrameItemClickListener() {
        Toast.makeText(this, "bigImage", Toast.LENGTH_SHORT).show()
    }

    private fun createObservingData(movieId: Long) {
        observeMovie(movieId)
        observeFrames(movieId)
        observeVideos(movieId)
//        observeFacts()
//        observeSimilars()
    }

    private fun observeMovie(movieId: Long) {
        detailViewModel.getMovie(movieId).observe(this) {
            this.movie = it
            setPoster()
            setNumber()
            setRating()
            setFavourite()
            setNameOrigin()
            setNameRu()
            setSlogan()
            setYear()
            setFilmLength()
            setCountries()
            setGenres()
            setDescription()
            setButtonKinopoisk()
        }
    }

    private fun observeFrames(movieId: Long) {
        detailViewModel.getFrames(movieId).observe(this) {
            if (it == null) {
                detailViewModel.loadFrames(movieId)
            } else {
                val list = it.frames
                if (list.isNotEmpty()) {
                    frameAdapter.setFrames(list)
                    recyclerViewFrames.visibility = VISIBLE
                }
            }
        }
    }

    private fun observeVideos(movieId: Long) {
        detailViewModel.getVideos(movieId).observe(this) {
            if (it == null) {
                detailViewModel.loadVideos(movieId)
            } else {
                val list = it.items
                if (list.isNotEmpty()) {
                    videoAdapter.setVideo(list)
                    textViewVideosL.visibility = VISIBLE
                    recyclerViewVideos.visibility = VISIBLE
                }
            }
        }
    }

//    private fun observeFacts() {
//        detailViewModel.getFacts().observe(this) {
//
//        }
//    }
//
//    private fun observeSimilars() {
//        detailViewModel.getSimilars()
//    }

    private fun setPoster() {
        Glide.with(this)
            .load(movie.poster)
            .placeholder(R.drawable.placeholder_image_poster)
            .override(POSTER_WIDTH, POSTER_HEIGHT)
            .centerCrop()
            .into(imageViewPoster)
    }

    private fun setNumber() {
        val numberPop = movie.topPopularPlace
        val number250 = movie.top250Place
        val numberAwait = movie.topAwaitPlace
        if (numberPop != 0) {
            val number = "$numberPop"
            textViewNumberPop.text = number
            textViewNumberPop.visibility = VISIBLE
            textViewNumberPopL.visibility = VISIBLE
        }
        if (number250 != 0) {
            val number = "$number250"
            textViewNumber250.text = number
            textViewNumber250.visibility = VISIBLE
            textViewNumber250L.visibility = VISIBLE
        }
        if (numberAwait != 0) {
            val number = "$numberAwait"
            textViewNumberAwait.text = number
            textViewNumberAwait.visibility = VISIBLE
            textViewNumberAwaitL.visibility = VISIBLE
        }
    }

    private fun setRating() {
        val rating = movie.rating
        rating?.let {
            textViewRating.text = it
            setRatingColor(textViewRating, it)
            textViewRating.visibility = VISIBLE
            val ratingCount = movie.ratingCount
            ratingCount?.let {
                textViewVotesCount.text = ratingCount
                textViewVotesCount.visibility = VISIBLE
            }
        }
    }

    private fun setRatingColor(view: TextView, rating: String) {
        when {
            rating.contains(Regex("^[7-9]")) -> view.setBackgroundResource(R.drawable.rounded_corner_green)
            rating.contains(Regex("^[56]")) -> view.setBackgroundResource(R.drawable.rounded_corner_grey)
            rating.contains(Regex("^[1-4]")) -> view.setBackgroundResource(R.drawable.rounded_corner_red)
        }
    }

    private fun setFavourite() {
        if (movie.isFavourite) setStarOrange()
        else setStarGrey()
    }

    private fun setStarGrey() {
        imageViewStar.setColorFilter(
            ContextCompat.getColor(this, android.R.color.darker_gray)
        )
        imageViewStar.visibility = VISIBLE
    }

    private fun setStarOrange() {
        imageViewStar.setColorFilter(
            ContextCompat.getColor(this, android.R.color.holo_orange_light)
        )
        imageViewStar.visibility = VISIBLE
    }

    private fun setNameOrigin() {
        val nameOriginal = movie.nameOriginal
        nameOriginal?.let {
            textViewNameOrig.text = it
            textViewNameOrig.visibility = VISIBLE
        }
    }

    private fun setNameRu() {
        val nameRu = movie.nameRu
        nameRu?.let {
            textViewNameRu.text = it
            textViewNameRu.visibility = VISIBLE
        }
    }

    private fun setSlogan() {
        val slogan = movie.slogan
        slogan?.let {
            val text = "\"$it\""
            textViewSlogan.text = text
            textViewSlogan.visibility = VISIBLE
        }
    }

    private fun setYear() {
        val year = movie.year
        year?.let {
            textViewYear.text = it
            textViewYearL.visibility = VISIBLE
            textViewYear.visibility = VISIBLE
        }
    }

    private fun setFilmLength() {
        val length = movie.length
        length?.let {
            val lengthForm = formatLength(it)
            textViewFilmLength.text = lengthForm
            textViewLengthL.visibility = VISIBLE
            textViewFilmLength.visibility = VISIBLE
        }
    }

    private fun formatLength(length: Int): String {
        val hours = length / 60
        val minutes = length % 60
        return "$hours:$minutes"
    }

    private fun setCountries() {
        if (movie.countries.isNotEmpty()) {
            val countriesString = getCountriesString(movie.countries)
            textViewCountries.text = countriesString
            textViewCountriesL.visibility = VISIBLE
            textViewCountries.visibility = VISIBLE
        }
    }

    private fun getCountriesString(countries: List<Country>): StringBuilder {
        val countriesString = StringBuilder()
        for ((i, country) in countries.withIndex()) {
            val one = country.name
            if (i == countries.size - 1) countriesString.append(one)
            else countriesString.append("$one\n")
        }
        return countriesString
    }

    private fun setGenres() {
        if (movie.genres.isNotEmpty()) {
            val genresString = getGenresString(movie.genres)
            textViewGenre.text = genresString
            textViewGenreL.visibility = VISIBLE
            textViewGenre.visibility = VISIBLE
        }
    }

    private fun getGenresString(genres: List<Genre>): StringBuilder {
        val genresString = StringBuilder()
        for ((i, genre) in genres.withIndex()) {
            val one = genre.name
            if (i == genres.size - 1) genresString.append(one)
            else genresString.append("$one\n")
        }
        return genresString
    }

    private fun setDescription() {
        val description = movie.description
        description?.let {
            textViewDescription.text = it
            textViewDescription.visibility = VISIBLE
        }
    }

    private fun setButtonKinopoisk() {
        textViewKinopoisk.visibility = VISIBLE
    }
}