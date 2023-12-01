package com.skid.headlines.headlines;

import static com.skid.utils.Constants.PAGE_SIZE;

import androidx.annotation.NonNull;

import com.skid.headlines.HeadlinesRouter;
import com.skid.news.model.Article;
import com.skid.news.repository.NewsRepository;
import com.skid.paging.Pager;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import moxy.MvpPresenter;


public class HeadlinesPresenter extends MvpPresenter<HeadlinesView> {

    static final String TAG = "HeadlinesPresenter";

    private final NewsRepository newsRepository;

    private final HeadlinesRouter router;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final BehaviorSubject<String> query = BehaviorSubject.createDefault("");

    private final BehaviorSubject<Pager<Article>> pager = BehaviorSubject.createDefault(getNewPager(query.getValue()));

    @Inject
    public HeadlinesPresenter(NewsRepository newsRepository, HeadlinesRouter router) {
        this.newsRepository = newsRepository;
        this.router = router;

        disposables.add(query
                .debounce(200, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(query -> pager.onNext(getNewPager(query)))
        );

        disposables.add(pager
                .switchMap(Pager::loadNextPage)
                .doOnEach(pagingData -> getViewState().submitPage(pagingData.getValue()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }

    private Pager<Article> getNewPager(@NonNull String query) {
        return new Pager<>(PAGE_SIZE, 1, () -> newsRepository.newsByQueryPagingSource(1, query));
    }


    public void onQueryChanged(@NonNull String query) {
        this.query.onNext(query);
    }

    public void onLoadNextPage() {
        pager.onNext(pager.getValue());
    }

    void onArticleProfile(Article article) {
        router.onArticleProfile(article);
    }

}
