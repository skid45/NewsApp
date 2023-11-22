package com.skid.headlines.newsbycategory;

import com.skid.news.repository.NewsRepository;

import javax.inject.Inject;

import moxy.MvpPresenter;

public class NewsByCategoryPresenter extends MvpPresenter<NewsByCategoryView> {

    private final NewsRepository newsRepository;

    @Inject
    public NewsByCategoryPresenter(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }



}
