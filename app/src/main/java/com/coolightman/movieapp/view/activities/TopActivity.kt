package com.coolightman.movieapp.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.coolightman.movieapp.R
import com.coolightman.movieapp.model.data.Favorite
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.view.adapters.TopAdapter
import com.coolightman.movieapp.viewmodel.TopViewModel
import kotlinx.android.synthetic.main.activity_top.*

class TopActivity : AppCompatActivity() {

    private lateinit var topViewModel: TopViewModel
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var topAdapter: TopAdapter

    companion object {
        private const val IMAGE_WIDTH = 360
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)
        topViewModel = ViewModelProvider(this).get(TopViewModel::class.java)

        createTopRecyclerView()
        createObservingData()
        listeners()
    }

    private fun createTopRecyclerView() {
        createLayoutManager()
        createAdapter()
    }

    private fun createAdapter() {
        topAdapter = TopAdapter { onItemClickListener(it) }
        recyclerViewTops.adapter = topAdapter
    }

    private fun createLayoutManager() {
        layoutManager = GridLayoutManager(this, getColumnCount())
        recyclerViewTops.layoutManager = layoutManager
    }

    private fun getColumnCount(): Int {
        val displayWidth = resources.displayMetrics.widthPixels
        val columns = displayWidth / IMAGE_WIDTH

        return if (columns > 2) columns
        else 2
    }

    private fun onItemClickListener(it: Movie) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("id", it.movieId)
        startActivity(intent)
    }

    private fun createObservingData() {

    }

    private fun listeners() {

    }
}