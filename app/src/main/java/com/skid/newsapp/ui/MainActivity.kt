package com.skid.newsapp.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.skid.headlines.headlines.HeadlinesFragment
import com.skid.newsapp.R
import com.skid.newsapp.appComponent
import com.skid.newsapp.databinding.ActivityMainBinding
import com.skid.newsapp.ui.navigation.Screens
import com.skid.saved.SavedFragment
import com.skid.sources.SourcesFragment
import com.skid.ui.resolveAttributeColor
import com.skid.utils.Constants.SELECTED_ITEM_ID_KEY
import com.skid.utils.collectFlow
import javax.inject.Inject
import javax.inject.Provider


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var viewModelProvider: Provider<MainViewModel.Factory>
    private val mainViewModel: MainViewModel by viewModels { viewModelProvider.get() }

    private val navigator =
        object : AppNavigator(this, R.id.activity_main_fragment_container_view) {
            override fun setupFragmentTransaction(
                screen: FragmentScreen,
                fragmentTransaction: FragmentTransaction,
                currentFragment: Fragment?,
                nextFragment: Fragment,
            ) {
                if (nextFragment::class !in bottomBarFragmentsSet) {
                    fragmentTransaction.setCustomAnimations(
                        /* enter = */ com.skid.ui.R.anim.slide_in_right,
                        /* exit = */ com.skid.ui.R.anim.slide_out_left,
                        /* popEnter = */ com.skid.ui.R.anim.slide_in_left,
                        /* popExit = */ com.skid.ui.R.anim.slide_out_right
                    )
                }
            }
        }

    private var filtersBadgeDrawable: BadgeDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.activityMainToolbar)

        setupToolbar()
        setupBottomNavigation()
        collectNumberOfFilters()
        setupBackStackListener()

        if (savedInstanceState == null) {
            binding.activityMainBottomNavigationView.selectedItemId = R.id.bottom_menu_headlines
        }
    }

    private fun setupBottomNavigation() = with(binding) {
        activityMainBottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_menu_headlines -> router.newRootScreen(Screens.HeadlinesScreen)
                R.id.bottom_menu_saved -> router.newRootScreen(Screens.SavedScreen)
                R.id.bottom_menu_sources -> router.newRootScreen(Screens.SourcesScreen)
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
        if (supportFragmentManager.backStackEntryCount == 0) {
            binding.activityMainBottomNavigationView.selectedItemId =
                savedInstanceState?.getInt(SELECTED_ITEM_ID_KEY) ?: R.id.bottom_menu_headlines
        }
    }

    @OptIn(ExperimentalBadgeUtils::class)
    private fun setupToolbar() {
        supportActionBar?.setHomeAsUpIndicator(com.skid.ui.R.drawable.ic_arrow_back)
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(com.skid.ui.R.menu.main_activity_menu, menu)
                val searchItem = menu.findItem(com.skid.ui.R.id.main_activity_menu_search)
                val searchView = searchItem.actionView as SearchView
                val searchEditText =
                    searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
                val searchCloseButton =
                    searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)

                val outline =
                    resolveAttributeColor(com.google.android.material.R.attr.colorOutline)
                searchEditText.setTextAppearance(com.skid.ui.R.style.TextAppearance_NewsApp_BodyLarge_White)
                searchEditText.setHintTextColor(outline)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    searchEditText.textCursorDrawable =
                        AppCompatResources.getDrawable(this@MainActivity, R.drawable.cursor)
                }
                DrawableCompat.setTint(searchCloseButton.drawable, Color.WHITE)

                filtersBadgeDrawable = BadgeDrawable.create(this@MainActivity).apply {
                    isVisible = mainViewModel.numberOfFilters.value > 0
                    number = mainViewModel.numberOfFilters.value
                }
                BadgeUtils.attachBadgeDrawable(
                    filtersBadgeDrawable!!,
                    binding.activityMainToolbar,
                    com.skid.ui.R.id.main_activity_menu_filter
                )
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    com.skid.ui.R.id.main_activity_menu_filter -> {
                        router.navigateTo(Screens.FiltersScreen)
                        true
                    }

                    android.R.id.home -> {
                        onBackPressedDispatcher.onBackPressed()
                        true
                    }

                    else -> false
                }
            }

        })
    }

    private fun setupBackStackListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount < 1) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            } else supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun collectNumberOfFilters() {
        collectFlow(mainViewModel.numberOfFilters) { numberOfFilters ->
            filtersBadgeDrawable?.apply {
                isVisible = numberOfFilters > 0
                number = numberOfFilters
            }
        }
    }

}

private val bottomBarFragmentsSet = setOf(
    HeadlinesFragment::class,
    SavedFragment::class,
    SourcesFragment::class
)