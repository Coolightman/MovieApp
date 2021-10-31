package com.coolightman.movieapp.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.coolightman.movieapp.R
import com.coolightman.movieapp.model.data.Frame

class GalleryAdapter : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {
    private var frames = listOf<Frame>()

    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val frameImg: ImageView = itemView.findViewById(R.id.imageViewGalleryImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.gallery_frame_item, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val frame = frames[position]
        val image = holder.frameImg

        Glide.with(holder.itemView.context)
            .load(frame.image)
            .into(image)
    }

    override fun getItemCount(): Int {
        return frames.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFrames(frames: List<Frame>) {
        this.frames = frames
        notifyDataSetChanged()
    }
}