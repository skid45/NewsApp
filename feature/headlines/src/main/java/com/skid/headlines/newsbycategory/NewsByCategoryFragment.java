package com.skid.headlines.newsbycategory;

import static com.skid.utils.Constants.CATEGORY_KEY;
import static com.skid.utils.Constants.ERROR_RESULT_KEY;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skid.headlines.databinding.FragmentNewsByCategoryBinding;
import com.skid.headlines.di.HeadlinesComponentViewModel;
import com.skid.news.model.Article;
import com.skid.paging.PagingAdapter;
import com.skid.paging.PagingData;
import com.skid.ui.databinding.ArticleItemBinding;
import com.skid.utils.Lazy;

import javax.inject.Inject;
import javax.inject.Provider;

import coil.Coil;
import coil.request.ImageRequest;
import kotlin.Unit;
import moxy.MvpAppCompatFragment;
import moxy.presenter.InjectPresenter;
import moxy.presenter.ProvidePresenter;

public class NewsByCategoryFragment extends MvpAppCompatFragment implements NewsByCategoryView {

    private FragmentNewsByCategoryBinding binding;

    private final Lazy<PagingAdapter<Article, ArticleItemBinding>> lazyArticlePagingAdapter = new Lazy<>(
            () -> new PagingAdapter<>(
                    new ArticleDiffCallback(),
                    (inflater, parent) -> ArticleItemBinding.inflate(inflater, parent, false),
                    (articleItemBinding, article) -> {
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
                    },
                    parent -> LayoutInflater.from(parent.getContext()),
                    message -> null
            )
    );

    @InjectPresenter(tag = NewsByCategoryPresenter.TAG)
    NewsByCategoryPresenter presenter;

    @Inject
    Provider<NewsByCategoryPresenter.Factory> presenterFactoryProvider;

    @ProvidePresenter(tag = NewsByCategoryPresenter.TAG)
    NewsByCategoryPresenter providePresenter() {
        return presenterFactoryProvider.get().create(requireArguments().getString(CATEGORY_KEY));
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNewsByCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupErrorResultListener();
        setupRecyclerView();
        setupSwipeRefreshLayout();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupRecyclerView() {
        binding.newsByCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.newsByCategoryRecyclerView.setAdapter(lazyArticlePagingAdapter.get());
        binding.newsByCategoryRecyclerView.addItemDecoration(
                new DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        );

        lazyArticlePagingAdapter.get().addOnLoadMoreListener(() -> {
            presenter.onLoadNextPage();
            return Unit.INSTANCE;
        });
    }

    private void setupSwipeRefreshLayout() {
        binding.newsByCategorySwipeRefreshLayout.setOnRefreshListener(() -> presenter.onRefresh());
    }

    private void setupErrorResultListener() {
        getParentFragmentManager().setFragmentResultListener(
                ERROR_RESULT_KEY,
                getViewLifecycleOwner(),
                (requestKey, result) -> {
                    showProgress(true);
                    presenter.onRefresh();
                }
        );
    }

    @Override
    public void showProgress(boolean isVisible) {
        if (isVisible) {
            binding.newsByCategoryProgressBar.setVisibility(View.VISIBLE);
            binding.newsByCategorySwipeRefreshLayout.setVisibility(View.GONE);
        } else {
            binding.newsByCategoryProgressBar.setVisibility(View.GONE);
            binding.newsByCategorySwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideRefresh() {
        binding.newsByCategorySwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void submitPage(PagingData<Article> page) {
        lazyArticlePagingAdapter.get().submitPage(page);
    }

    @Override
    public void onArticleProfile(Article article) {
        presenter.onArticleProfile(article);
    }
}