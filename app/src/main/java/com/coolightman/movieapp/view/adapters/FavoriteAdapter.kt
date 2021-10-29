package com.coolightman.movieapp.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.coolightman.movieapp.R
import com.coolightman.movieapp.model.data.Favorite

class FavoriteAdapter(private val listener: (Favorite) -> Unit):RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {
    private var favorites = listOf<Favorite>()

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val preview: ImageView = itemView.findViewById(R.id.imageViewPreview)
        val rating: TextView = itemView.findViewById(R.id.textViewRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.movie_item, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favorites[position]
        setImage(favorite, holder)
        setRating(favorite, holder)
        holder.itemView.setOnClickListener { listener(favorite) }
    }

    private fun setImage(favorite: Favorite, holder: FavoriteViewHolder) {
        Glide.with(holder.itemView.context)
            .load(favorite.preview)
            .placeholder(R.drawable.placeholder_image)
            .centerCrop()
            .into(holder.preview)
    }

    private fun setRating(favorite: Favorite, holder: FavoriteViewHolder) {
        val rating = favorite.rating
        rating?.let {
            holder.rating.text = rating
            holder.rating.visibility = View.VISIBLE
            setRatingColor(holder.rating, rating)
        }
    }

    private fun setRatingColor(view: TextView, rating: String) {
        when {
            rating.contains(Regex("^[7-9]")) -> view.setBackgroundResource(R.drawable.rounded_corner_green)
            rating.contains(Regex("^[56]")) -> view.setBackgroundResource(R.drawable.rounded_corner_grey)
            rating.contains(Regex("^[1-4]")) -> view.setBackgroundResource(R.drawable.rounded_corner_red)
        }
    }

    override fun getItemCount(): Int {
        return favorites.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFavorites(favorites: List<Favorite>){
        this.favorites = favorites
        notifyDataSetChanged()
    }
}