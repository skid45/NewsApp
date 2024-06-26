package com.skid.headlines.newsbycategory;

import com.skid.news.model.Article;
import com.skid.paging.PagingData;

import moxy.MvpView;
import moxy.viewstate.strategy.alias.AddToEnd;
import moxy.viewstate.strategy.alias.AddToEndSingle;


public interface NewsByCategoryView extends MvpView {

    @AddToEndSingle
    void showProgress(boolean isVisible);

    @AddToEndSingle
    void hideRefresh();

    @AddToEnd
    void submitPage(PagingData<Article> page);

    @AddToEndSingle
    void onArticleProfile(Article article);
}
