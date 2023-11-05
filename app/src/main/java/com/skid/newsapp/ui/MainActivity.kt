package com.skid.newsapp.ui

import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuProvider
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.skid.newsapp.R
import com.skid.newsapp.appComponent
import com.skid.newsapp.databinding.ActivityMainBinding
import com.skid.newsapp.ui.navigation.Screens
import com.skid.newsapp.utils.Constants.SELECTED_ITEM_ID_KEY
import com.skid.newsapp.utils.resolveAttributeColor
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

        setupToolbar()
        setupBottomNavigation()

        if (savedInstanceState == null) {
            binding.activityMainBottomNavigationView.selectedItemId = R.id.bottom_menu_headlines
        }

    }

    private fun setupBottomNavigation() = with(binding) {
        activityMainBottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_menu_headlines -> {} //router.replaceScreen(TODO("HeadlinesFragment"))
                R.id.bottom_menu_saved -> {} //router.replaceScreen(TODO("HeadlinesFragment"))
                R.id.bottom_menu_sources -> router.replaceScreen(Screens.SourcesScreen)
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

    private fun setupToolbar() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_activity_menu, menu)
                val searchView =
                    menu.findItem(R.id.main_activity_menu_search).actionView as SearchView
                val searchEditText =
                    searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
                val searchCloseButton =
                    searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)

                val onPrimary =
                    resolveAttributeColor(com.google.android.material.R.attr.colorOnPrimary)
                val surfaceVariant =
                    resolveAttributeColor(com.google.android.material.R.attr.colorSurfaceVariant)
                searchEditText.setTextAppearance(R.style.TextAppearance_NewsApp_BodyLarge_OnPrimary)
                searchEditText.setHintTextColor(surfaceVariant)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    searchEditText.textCursorDrawable =
                        AppCompatResources.getDrawable(this@MainActivity, R.drawable.cursor)
                }
                DrawableCompat.setTint(searchCloseButton.drawable, onPrimary)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.main_activity_menu_filter -> {
                        router.navigateTo(Screens.FiltersScreen)
                        true
                    }

                    else -> false
                }
            }
        })
    }


}