package com.coolightman.movieapp.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.coolightman.movieapp.R

class FavoriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
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