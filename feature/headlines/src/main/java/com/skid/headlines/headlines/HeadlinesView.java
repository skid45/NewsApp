package com.skid.headlines.headlines;

import com.skid.news.model.Article;
import com.skid.paging.PagingData;

import moxy.MvpView;
import moxy.viewstate.strategy.alias.AddToEnd;
import moxy.viewstate.strategy.alias.AddToEndSingle;

public interface HeadlinesView extends MvpView {

    @AddToEnd
    void submitPage(PagingData<Article> pagingData);

    @AddToEndSingle
    void onSearchExpand();

    @AddToEndSingle
    void onSearchCollapse();

    @AddToEndSingle
    void onArticleProfile(Article article);
}
