package com.coolightman.movieapp.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coolightman.movieapp.R
import com.coolightman.movieapp.model.data.Movie
import com.coolightman.movieapp.model.enums.Top
import com.coolightman.movieapp.view.adapters.TopAdapter
import com.coolightman.movieapp.viewmodel.TopViewModel
import kotlinx.android.synthetic.main.activity_top.*

class TopActivity : AppCompatActivity() {

    private lateinit var topViewModel: TopViewModel
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var topAdapter: TopAdapter
    private lateinit var topType: Top

    private var isScrollDownloading = false
    private var prevPopularCount = 0
    private var prev250Count = 0
    private var prevAwaitCount = 0

    private var resettingPrevPopularCount = 0
    private var resettingPrev250Count = 0
    private var resettingPrevAwaitCount = 0

    companion object {
        private const val IMAGE_WIDTH = 360
        private const val SCROLL_LOADER_RESERVE = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
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
        topViewModel.getTopMovies().observe(this) {
            topAdapter.setMovies(it)
            resetPreviousTotalItemCount()
            isScrollDownloading = false
            spinnerTops.isEnabled = true
            progressBarLoading.visibility = GONE
        }
    }

    private fun listeners() {
        spinnerListener()
        scrollListener()

        imageViewRefresh.setOnClickListener {
            topViewModel.refreshData()
        }
    }

    private fun spinnerListener() {
        spinnerTops.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
//                topAdapter.clearAdapter()
                setCurrentTopType(position)
                layoutManager.scrollToPosition(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.i("spinnerTops", "NothingSelected")
            }
        }
    }

    private fun setCurrentTopType(position: Int) {
        val topType = when (position) {
            0 -> Top.TOP_100_POPULAR_FILMS
            1 -> Top.TOP_250_BEST_FILMS
            else -> Top.TOP_AWAIT_FILMS
        }
        this.topType = topType
        topViewModel.setTopType(topType)
        topAdapter.setTopType(topType)
    }

    private fun scrollListener() {
        recyclerViewTops
            .addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val totalItemCount = layoutManager.itemCount
                    val previousTotalItemCount = getPreviousTotal()
                    val checkedSum = getCheckedSum()

                    if (!isScrollDownloading &&
                        totalItemCount > previousTotalItemCount &&
                        checkedSum > totalItemCount
                    ) {
                        Log.e("Scroll", "triggered")
                        isScrollDownloading = true
                        spinnerTops.isEnabled = false
                        progressBarLoading.visibility = VISIBLE
                        setResettingPrevCount(totalItemCount)
                        topViewModel.loadNextPage()
                    }
                }
            })
    }

    private fun setResettingPrevCount(totalItemCount: Int) {
        when (topType) {
            Top.TOP_100_POPULAR_FILMS -> resettingPrevPopularCount = totalItemCount
            Top.TOP_250_BEST_FILMS -> resettingPrev250Count = totalItemCount
            Top.TOP_AWAIT_FILMS -> resettingPrevAwaitCount = totalItemCount
        }
    }

    private fun getCheckedSum(): Int {
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val visibleItemCount = layoutManager.childCount
        val reserve = SCROLL_LOADER_RESERVE
        return firstVisibleItemPosition + visibleItemCount + reserve
    }

    private fun getPreviousTotal(): Int {
        return when (topType) {
            Top.TOP_100_POPULAR_FILMS -> prevPopularCount
            Top.TOP_250_BEST_FILMS -> prev250Count
            Top.TOP_AWAIT_FILMS -> prevAwaitCount
        }
    }

    private fun resetPreviousTotalItemCount() {
        when (topType) {
            Top.TOP_100_POPULAR_FILMS -> prevPopularCount = resettingPrevPopularCount
            Top.TOP_250_BEST_FILMS -> prev250Count = resettingPrev250Count
            Top.TOP_AWAIT_FILMS -> prevAwaitCount = resettingPrevAwaitCount
        }
    }

}