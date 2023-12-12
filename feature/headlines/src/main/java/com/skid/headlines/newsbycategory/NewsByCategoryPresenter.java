package com.skid.headlines.newsbycategory;

import static com.skid.utils.Constants.PAGE_SIZE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skid.filters.model.Filters;
import com.skid.filters.model.Language;
import com.skid.filters.model.Sorting;
import com.skid.filters.repository.FiltersRepository;
import com.skid.headlines.HeadlinesRouter;
import com.skid.news.model.Article;
import com.skid.news.repository.NewsRepository;
import com.skid.paging.Pager;
import com.skid.paging.PagingData;
import com.skid.utils.ExtensionsKt;

import java.util.Calendar;
import java.util.Locale;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import kotlin.Pair;
import moxy.MvpPresenter;

public class NewsByCategoryPresenter extends MvpPresenter<NewsByCategoryView> {

    static final String TAG = "NewsByCategoryPresenter";

    private final NewsRepository newsRepository;

    private final FiltersRepository filtersRepository;

    private final HeadlinesRouter router;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final String category;

    private final BehaviorSubject<Pager<Article>> pager;

    private final BehaviorSubject<Filters> filters = BehaviorSubject
            .createDefault(new Filters(Sorting.NEW, null, null, 0));

    @AssistedInject
    public NewsByCategoryPresenter(
            NewsRepository newsRepository,
            FiltersRepository filtersRepository,
            HeadlinesRouter router,
            @Assisted String category
    ) {
        this.newsRepository = newsRepository;
        this.filtersRepository = filtersRepository;
        this.router = router;
        this.category = category;

        this.pager = BehaviorSubject
                .createDefault(getNewPager(category, null, null, null));
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().showProgress(true);

        disposables.add(
                ExtensionsKt.asObservable(filtersRepository.getFilters())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this.filters::onNext)
        );

        disposables.add(
                filters.subscribe(filters -> pager.onNext(
                        getNewPager(
                                category,
                                filters.getSortBy(),
                                filters.getChosenDates(),
                                filters.getLanguage()
                        )
                ))
        );

        disposables.add(pager
                .switchMap(Pager::loadNextPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pagingData -> {
                    if (pagingData instanceof PagingData.Error) {
                        onError(pagingData.getError());
                    }
                    getViewState().showProgress(false);
                    getViewState().submitPage(pagingData);
                    getViewState().hideRefresh();
                })
        );
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }

    private Pager<Article> getNewPager(
            @NonNull String category,
            @Nullable Sorting sortBy,
            @Nullable Pair<Calendar, Calendar> chosenDates,
            @Nullable Language language
    ) {
        return new Pager<>(PAGE_SIZE, 1, () -> newsRepository.newsByCategoryPagingSource(
                1,
                category,
                sortBy != null ? sortBy.getApiName() : null,
                chosenDates != null ? ExtensionsKt.format(chosenDates.getFirst(), "yyyy MM dd", Locale.getDefault()) : null,
                chosenDates != null ? ExtensionsKt.format(chosenDates.getSecond(), "yyyy MM dd", Locale.getDefault()) : null,
                language != null ? language.getApiName() : null
        ));
    }

    void onRefresh() {
        filters.onNext(filters.getValue());
    }

    void onLoadNextPage() {
        pager.onNext(pager.getValue());
    }

    void onArticleProfile(Article article) {
        router.onArticleProfile(article);
    }

    void onError(String message) {
        router.onError(message);
    }


    @AssistedFactory
    interface Factory {
        NewsByCategoryPresenter create(String category);
    }
}
