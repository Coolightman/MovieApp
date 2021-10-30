package com.coolightman.movieapp.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.coolightman.movieapp.R
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.model.enums.Top

class TopAdapter(private val listener: (Movie) -> Unit) :
    RecyclerView.Adapter<TopAdapter.TopViewHolder>() {

    private var movies = listOf<Movie>()
    private lateinit var topType: Top

    class TopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val preview: ImageView = itemView.findViewById(R.id.imageViewPreview)
        val number: TextView = itemView.findViewById(R.id.textViewNumberPop)
        val rating: TextView = itemView.findViewById(R.id.textViewRating)
        val favourite: ImageView = itemView.findViewById(R.id.imageViewFavourite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.movie_item, parent, false)
        return TopViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopViewHolder, position: Int) {
        val movie = movies[position]
        setItemView(movie, holder)
        holder.itemView.setOnClickListener { listener(movie) }
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMovies(list: List<Movie>) {
        this.movies = list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearAdapter(){
        this.movies = emptyList()
        notifyDataSetChanged()
    }

    fun setTopType(topType: Top) {
        this.topType = topType
    }

    private fun setItemView(movie: Movie, holder: TopViewHolder) {
        setImage(movie, holder)
        setNumber(movie, holder)
        setRating(movie, holder)
        setFavourite(movie, holder)
    }

    private fun setImage(movie: Movie, holder: TopViewHolder) {
        Glide.with(holder.itemView.context)
            .load(movie.preview)
            .placeholder(R.drawable.placeholder_image)
            .centerCrop()
            .into(holder.preview)
    }

    private fun setNumber(movie: Movie, holder: TopViewHolder) {
        val number = when (topType) {
            Top.TOP_100_POPULAR_FILMS -> "${movie.topPopularPlace}"
            Top.TOP_250_BEST_FILMS -> "${movie.top250Place}"
            Top.TOP_AWAIT_FILMS -> "${movie.topAwaitPlace}"
        }
        holder.number.setBackgroundResource(R.drawable.rounded_corner_number)
        holder.number.visibility = VISIBLE
        holder.number.text = number
    }

    private fun setRating(movie: Movie, holder: TopViewHolder) {
        val rating = movie.rating
        rating?.let {
            holder.rating.text = it
            setRatingColor(holder.rating, it)
            holder.rating.visibility = VISIBLE
        }
    }

    private fun setRatingColor(view: TextView, rating: String) {
        when {
            rating.contains(Regex("^[7-9]")) -> view.setBackgroundResource(R.drawable.rounded_corner_green)
            rating.contains(Regex("^[56]")) -> view.setBackgroundResource(R.drawable.rounded_corner_grey)
            rating.contains(Regex("^[2-4]")) -> view.setBackgroundResource(R.drawable.rounded_corner_red)
        }
    }

    private fun setFavourite(movie: Movie, holder: TopViewHolder) {
        if (movie.isFavourite) {
            holder.favourite.visibility = VISIBLE
        } else {
            holder.favourite.visibility = GONE
        }
    }
}