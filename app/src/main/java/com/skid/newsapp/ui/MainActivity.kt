package com.skid.newsapp.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.skid.newsapp.R
import com.skid.newsapp.appComponent
import com.skid.newsapp.databinding.ActivityMainBinding
import com.skid.newsapp.utils.Constants.SELECTED_ITEM_ID_KEY
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    private val navigator = AppNavigator(this, R.id.activity_main_fragment_container_view)

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.activityMainToolbar)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            binding.activityMainBottomNavigationView.selectedItemId = R.id.bottom_menu_headlines
        }

    }


    private fun setupBottomNavigation() = with(binding) {
        activityMainBottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_menu_headlines -> router.replaceScreen(TODO("HeadlinesFragment"))
                R.id.bottom_menu_saved -> router.replaceScreen(TODO("HeadlinesFragment"))
                R.id.bottom_menu_sources -> router.replaceScreen(TODO("SourcesFragment"))
                else -> return@setOnItemSelectedListener false
            }
            true
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(
            SELECTED_ITEM_ID_KEY,
            binding.activityMainBottomNavigationView.selectedItemId
        )
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?,
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        if (supportFragmentManager.backStackEntryCount == 0)
            binding.activityMainBottomNavigationView.selectedItemId =
                savedInstanceState?.getInt(SELECTED_ITEM_ID_KEY) ?: R.id.bottom_menu_headlines
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.main_activity_menu_filter -> {
                router.navigateTo(TODO("FilterScreen"))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}