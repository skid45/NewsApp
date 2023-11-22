package com.skid.headlines.newsbycategory;

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
import com.skid.ui.databinding.ArticleItemBinding;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import javax.inject.Provider;

import moxy.MvpAppCompatFragment;
import moxy.presenter.InjectPresenter;
import moxy.presenter.ProvidePresenter;

public class NewsByCategoryFragment extends MvpAppCompatFragment implements NewsByCategoryView {

    private FragmentNewsByCategoryBinding binding;

    private PagingAdapter<Article, ArticleItemBinding> articlePagingAdapter;

    @InjectPresenter
    NewsByCategoryPresenter presenter;

    @Inject
    Provider<NewsByCategoryPresenter> presenterProvider;

    @ProvidePresenter
    NewsByCategoryPresenter providePresenter() {
        return presenterProvider.get();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        new ViewModelProvider(this)
                .get(HeadlinesComponentViewModel.class)
                .getHeadlinesComponent()
                .inject(this);

        articlePagingAdapter = new PagingAdapter<>(
                new ArticleDiffCallback(),
                (inflater, parent) -> ArticleItemBinding.inflate(inflater, parent, false),
                (articleItemBinding, article) -> {
                    articleItemBinding.getRoot().setOnClickListener(v -> {

                    });
                    Picasso.get().load(article.getImageUrl()).into(articleItemBinding.articleItemImage);
                    articleItemBinding.articleItemSourceImage.setImageResource(article.getSourceDrawableId());
                    articleItemBinding.articleItemSourceName.setText(article.getSourceName());
                    articleItemBinding.articleItemTitle.setText(article.getTitle());
                    return null;
                },
                parent -> LayoutInflater.from(parent.getContext()),
                message -> null
        );
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

        setupRecyclerView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        articlePagingAdapter = null;
    }

    private void setupRecyclerView() {
        binding.newsByCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.newsByCategoryRecyclerView.setAdapter(articlePagingAdapter);
        binding.newsByCategoryRecyclerView.addItemDecoration(
                new DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        );
    }

}