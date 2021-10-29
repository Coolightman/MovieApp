package com.coolightman.movieapp.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.coolightman.movieapp.R
import com.coolightman.movieapp.model.data.*
import com.coolightman.movieapp.view.adapters.FactsAdapter
import com.coolightman.movieapp.view.adapters.FrameAdapter
import com.coolightman.movieapp.view.adapters.SimilarAdapter
import com.coolightman.movieapp.view.adapters.VideoAdapter
import com.coolightman.movieapp.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.movie_info.*

class DetailActivity : AppCompatActivity() {

    private lateinit var detailViewModel: DetailViewModel
    private lateinit var movie: Movie
    private lateinit var frameAdapter: FrameAdapter
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var factsAdapter: FactsAdapter
    private lateinit var similarAdapter: SimilarAdapter

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = when (item.itemId) {
            R.id.itemMain -> Intent(this, TopActivity::class.java)
            R.id.itemFavourite -> Intent(this, FavoriteActivity::class.java)
            else -> Intent(this, TopActivity::class.java)
        }
        startActivity(intent)
        return super.onOptionsItemSelected(item)
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
        createFactsAdapter()
        createSimilarsAdapter()
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

    private fun createFactsAdapter() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewFacts.layoutManager = layoutManager
        factsAdapter = FactsAdapter { onFactClickListener(it) }
        recyclerViewFacts.adapter = factsAdapter
    }

    private fun onFactClickListener(fact: Fact) {
        Toast.makeText(this, "Big fact", Toast.LENGTH_SHORT).show()
    }

    private fun createSimilarsAdapter() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewSimilars.layoutManager = layoutManager
        similarAdapter = SimilarAdapter { onSimilarClickListener(it) }
        recyclerViewSimilars.adapter = similarAdapter
    }

    private fun onSimilarClickListener(similar: Movie) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("id", similar.movieId)
        startActivity(intent)
    }

    private fun createObservingData(movieId: Long) {
        observeMovie(movieId)
        observeFrames(movieId)
        observeVideos(movieId)
        observeFacts(movieId)
        observeSimilars(movieId)
    }

    private fun observeMovie(movieId: Long) {
        detailViewModel.getMovie(movieId).observe(this) {
            it?.let {
                Log.e("ObservingMovie", it.toString())
                this.movie = it
                if (movie.isDetailed){
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
        }
    }

    private fun observeFrames(movieId: Long) {
        detailViewModel.getFrames(movieId).observe(this) {
            it?.let {
                val items = it.frames
                if (items.isNotEmpty()) {
                    frameAdapter.setFrames(items)
                    recyclerViewFrames.visibility = VISIBLE
                }
            }
        }
    }

    private fun observeVideos(movieId: Long) {
        detailViewModel.getVideos(movieId).observe(this) {
            it?.let {
                val items = it.items
                if (items.isNotEmpty()) {
                    videoAdapter.setVideo(items)
                    textViewVideosL.visibility = VISIBLE
                    recyclerViewVideos.visibility = VISIBLE
                }
            }
        }
    }

    private fun observeFacts(movieId: Long) {
        detailViewModel.getFacts(movieId).observe(this) {
            it?.let {
                val facts = it.items
                if (facts.isNotEmpty()) {
                    factsAdapter.setFacts(facts)
                    textViewFactsL.visibility = VISIBLE
                    recyclerViewFacts.visibility = VISIBLE
                }
            }
        }
    }

    private fun observeSimilars(movieId: Long) {
        detailViewModel.getSimilars(movieId).observe(this) {
            it?.let {
                val similars = it.items
                if (similars.isNotEmpty()) {
                    similarAdapter.setSimilars(similars)
                    textViewSimilarsL.visibility = VISIBLE
                    recyclerViewSimilars.visibility = VISIBLE
                }
            }
        }
    }

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