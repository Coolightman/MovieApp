package com.coolightman.movieapp.view.activities

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.coolightman.movieapp.R
import com.coolightman.movieapp.view.adapters.GalleryAdapter
import com.coolightman.movieapp.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.activity_gallery.*

class GalleryActivity : AppCompatActivity() {

    private lateinit var detailViewModel: DetailViewModel
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var layoutManager: LinearLayoutManager

    companion object {
        private var prevPosition = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

//        fullscreen mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        detailViewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        val movieId = intent.getLongExtra("movieId", -1)
        val position = intent.getIntExtra("position", -1)

        createAdapter()
        createObserver(movieId)

//        fix rotate installing start layout position
        if (position != prevPosition) {
            Handler(Looper.getMainLooper()).postDelayed({
                layoutManager.scrollToPosition(position)
                prevPosition = position
            }, 10)
        }
    }

    private fun createAdapter() {
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewGallery.layoutManager = layoutManager
        galleryAdapter = GalleryAdapter()
        recyclerViewGallery.adapter = galleryAdapter

//        remember position when rotate
        galleryAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

//        swipe by one item
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewGallery)
    }

    private fun createObserver(movieId: Long) {
        detailViewModel.getFrames(movieId).observe(this) {
            it?.let {
                val items = it.frames
                if (items.isNotEmpty()) {
                    galleryAdapter.setFrames(items)
                }
            }
        }
    }
}