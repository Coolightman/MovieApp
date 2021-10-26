package com.coolightman.movieapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coolightman.movieapp.R
import com.coolightman.movieapp.model.data.Video

class VideoAdapter(
    private val videos: List<Video>,
    private val listener: (Video) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trailerName: TextView = itemView.findViewById(R.id.textViewTrailerName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.video_item, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]
        holder.trailerName.text = video.name
        holder.itemView.setOnClickListener { listener(video) }
    }

    override fun getItemCount(): Int {
        return videos.size
    }
}