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
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.view.activities.DetailActivity

class SimilarAdapter(private val listener: (Movie) -> Unit) :
    RecyclerView.Adapter<SimilarAdapter.SimilarViewHolder>() {

    private var similars = listOf<Movie>()

    companion object{
        private const val PREVIEW_WIDTH = 360
        private const val PREVIEW_HEIGHT = 540
    }

    class SimilarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val preview: ImageView = itemView.findViewById(R.id.imageViewSimilarPreview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.similar_item, parent, false)
        return SimilarViewHolder(view)
    }

    override fun onBindViewHolder(holder: SimilarViewHolder, position: Int) {
        val similar = similars[position]
        setImage(similar, holder)
        holder.itemView.setOnClickListener { listener(similar) }
    }

    private fun setImage(similar: Movie, holder: SimilarViewHolder) {
        Glide.with(holder.itemView.context)
            .load(similar.preview)
            .placeholder(R.drawable.placeholder_image)
            .override(PREVIEW_WIDTH, PREVIEW_HEIGHT)
            .centerCrop()
            .into(holder.preview)
    }

    override fun getItemCount(): Int {
        return similars.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSimilars(similars: List<Movie>) {
        this.similars = similars
        notifyDataSetChanged()
    }
}