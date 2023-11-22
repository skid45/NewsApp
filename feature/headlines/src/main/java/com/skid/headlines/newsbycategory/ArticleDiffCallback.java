package com.skid.headlines.newsbycategory;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.skid.news.model.Article;

public class ArticleDiffCallback extends DiffUtil.ItemCallback<Article> {

    @Override
    public boolean areItemsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
        return oldItem.getUrl().equals(newItem.getUrl());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
        return oldItem.equals(newItem);
    }
}
