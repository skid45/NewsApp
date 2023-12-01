package com.skid.headlines.headlines;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayoutMediator;
import com.skid.headlines.databinding.FragmentHeadlinesBinding;
import com.skid.headlines.di.HeadlinesComponentViewModel;
import com.skid.headlines.newsbycategory.ArticleDiffCallback;
import com.skid.news.model.Article;
import com.skid.paging.PagingAdapter;
import com.skid.paging.PagingData;
import com.skid.ui.databinding.ArticleItemBinding;

import javax.inject.Inject;
import javax.inject.Provider;

import coil.Coil;
import coil.request.ImageRequest;
import kotlin.Unit;
import moxy.MvpAppCompatFragment;
import moxy.presenter.InjectPresenter;
import moxy.presenter.ProvidePresenter;

public class HeadlinesFragment extends MvpAppCompatFragment implements HeadlinesView {

    private FragmentHeadlinesBinding binding;

    private HeadlinesPagerAdapter headlinesPagerAdapter;

    private PagingAdapter<Article, ArticleItemBinding> headlinesByQueryAdapter;

    @InjectPresenter(tag = HeadlinesPresenter.TAG)
    HeadlinesPresenter presenter;

    @Inject
    Provider<HeadlinesPresenter> presenterProvider;

    @ProvidePresenter(tag = HeadlinesPresenter.TAG)
    HeadlinesPresenter providePresenter() {
        return presenterProvider.get();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        new ViewModelProvider(this)
                .get(HeadlinesComponentViewModel.class)
                .getHeadlinesComponent()
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentHeadlinesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        headlinesPagerAdapter = new HeadlinesPagerAdapter(
                this,
                getResources().getStringArray(com.skid.ui.R.array.tab_names)
        );

        headlinesByQueryAdapter = new PagingAdapter<>(
                new ArticleDiffCallback(),
                ((inflater, parent) -> ArticleItemBinding.inflate(inflater, parent, false)),
                ((articleItemBinding, article) -> {
                    articleItemBinding.getRoot().setOnClickListener(v -> onArticleProfile(article));
                    ImageRequest request = new ImageRequest.Builder(requireContext())
                            .data(article.getImageUrl())
                            .crossfade(true)
                            .target(articleItemBinding.articleItemImage)
                            .build();
                    Coil.imageLoader(requireContext()).enqueue(request);
                    articleItemBinding.articleItemSourceImage.setImageResource(article.getSourceDrawableId());
                    articleItemBinding.articleItemSourceName.setText(article.getSourceName());
                    articleItemBinding.articleItemTitle.setText(article.getTitle());
                    return null;
                }),
                (parent) -> LayoutInflater.from(parent.getContext()),
                (message) -> null
        );


        setupToolbar();
        setupRecyclerView();
        setupPager();
        setupTabs();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        headlinesPagerAdapter = null;
        headlinesByQueryAdapter = null;
    }

    private void setupRecyclerView() {
        binding.headlinesSearchRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.headlinesSearchRecyclerView.setAdapter(headlinesByQueryAdapter);
        binding.headlinesSearchRecyclerView.addItemDecoration(
                new DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        );

        headlinesByQueryAdapter.addOnLoadMoreListener(() -> {
            presenter.onLoadNextPage();
            return Unit.INSTANCE;
        });
    }

    private void setupPager() {
        binding.headlinesPager.setAdapter(headlinesPagerAdapter);
    }

    private void setupTabs() {
        String[] tabNames = getResources().getStringArray(com.skid.ui.R.array.tab_names);
        TypedArray tabIcons = getResources().obtainTypedArray(com.skid.ui.R.array.tab_icons);

        new TabLayoutMediator(binding.headlinesTabLayout, binding.headlinesPager, (tab, position) -> {
            tab.setText(tabNames[position]);
            tab.setIcon(AppCompatResources.getDrawable(requireContext(), tabIcons.getResourceId(position, 0)));
        }).attach();

        tabIcons.recycle();
    }

    private void setupToolbar() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity())
                .getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(com.skid.ui.R.string.sources);
        }

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(menuProvider, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private final MenuProvider menuProvider = new MenuProvider() {
        @Override
        public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            MenuItem searchItem = menu.findItem(com.skid.ui.R.id.main_activity_menu_search);
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        presenter.onQueryChanged(newText == null ? "" : newText);
                        return true;
                    }
                });
            }

            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                    onSearchExpand();
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                    onSearchCollapse();
                    return true;
                }
            });
        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
            return false;
        }
    };

    @Override
    public void submitPage(PagingData<Article> pagingData) {
        headlinesByQueryAdapter.submitPage(pagingData);
    }

    @Override
    public void onArticleProfile(Article article) {
        presenter.onArticleProfile(article);
    }

    @Override
    public void onSearchExpand() {
        binding.headlinesMainContent.setVisibility(View.GONE);
        binding.headlinesSearchRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSearchCollapse() {
        binding.headlinesMainContent.setVisibility(View.VISIBLE);
        binding.headlinesSearchRecyclerView.setVisibility(View.GONE);
    }
}
