package com.skid.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.skid.newsapp.R
import com.skid.newsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.activityMainBottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_menu_headlines -> TODO("Open HeadlinesFragment")
                R.id.bottom_menu_saved -> TODO("Open SavedFragment")
                R.id.bottom_menu_sources -> TODO("Open SourcesFragment")
                else -> return@setOnItemSelectedListener false
            }
            true
        }
    }
}