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

class FrameAdapter(private val listener: (Frame) -> Unit) :
    RecyclerView.Adapter<FrameAdapter.FrameViewHolder>() {
    private var frames = listOf<Frame>()

    class FrameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val frameImg: ImageView = itemView.findViewById(R.id.imageViewFrame)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrameViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.frame_item, parent, false)
        return FrameViewHolder(view)
    }

    override fun onBindViewHolder(holder: FrameViewHolder, position: Int) {
        val frame = frames[position]
        val imageViewFrame = holder.frameImg

        Glide.with(holder.itemView.context)
            .load(frame.preview)
            .placeholder(R.drawable.placeholder_image)
            .into(imageViewFrame)

        holder.itemView.setOnClickListener { listener(frame) }
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