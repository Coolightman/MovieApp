package com.coolightman.movieapp.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.coolightman.movieapp.R
import com.coolightman.movieapp.model.data.Favorite
import com.coolightman.movieapp.view.adapters.FavoriteAdapter
import com.coolightman.movieapp.view.adapters.VideoAdapter
import com.coolightman.movieapp.viewmodel.FavoriteViewModel
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.movie_info.*

class FavoriteActivity : AppCompatActivity() {
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteAdapter: FavoriteAdapter

    companion object {
        private const val IMAGE_WIDTH = 360
        private const val MIN_COLUMN = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        favoriteViewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)

        createObserver()
        createAdapter()
    }

    private fun createAdapter() {
        val layoutManager = GridLayoutManager(this, getColumnCount())
        recyclerViewFavorite.layoutManager = layoutManager
        favoriteAdapter = FavoriteAdapter { onFavoriteItemClickListener(it) }
        recyclerViewFavorite.adapter = favoriteAdapter
    }

    private fun getColumnCount(): Int {
        val displayWidth = resources.displayMetrics.widthPixels
        val columns = displayWidth / IMAGE_WIDTH

        return if (columns > MIN_COLUMN) columns
        else MIN_COLUMN
    }

    private fun onFavoriteItemClickListener(favorite: Favorite) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("id", favorite.movieId)
        startActivity(intent)
    }

    private fun createObserver() {
        favoriteViewModel.getFavorite().observe(this){
            it?.let {
                if (it.isNotEmpty()){
                    favoriteAdapter.setFavorites(it)
                    recyclerViewFavorite.visibility = VISIBLE
                }
            }
        }
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
}