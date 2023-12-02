package com.skid.headlines.newsbycategory;

import static com.skid.utils.Constants.PAGE_SIZE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skid.filters.model.Language;
import com.skid.filters.model.Sorting;
import com.skid.filters.repository.FiltersRepository;
import com.skid.headlines.HeadlinesRouter;
import com.skid.news.model.Article;
import com.skid.news.repository.NewsRepository;
import com.skid.paging.Pager;
import com.skid.utils.ExtensionsKt;

import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import kotlin.Pair;
import kotlin.Unit;
import moxy.MvpPresenter;

public class NewsByCategoryPresenter extends MvpPresenter<NewsByCategoryView> {

    static final String TAG = "NewsByCategoryPresenter";

    private final NewsRepository newsRepository;

    private final FiltersRepository filtersRepository;

    private final HeadlinesRouter router;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final BehaviorSubject<String> category = BehaviorSubject.createDefault("");

    private final BehaviorSubject<Pager<Article>> pager = BehaviorSubject
            .createDefault(getNewPager(category.getValue(), null, null, null));

    @Inject
    public NewsByCategoryPresenter(
            NewsRepository newsRepository,
            FiltersRepository filtersRepository,
            HeadlinesRouter router
    ) {
        this.newsRepository = newsRepository;
        this.filtersRepository = filtersRepository;
        this.router = router;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().showProgress(true);

        disposables.add(Observable
                .combineLatest(
                        category,
                        ExtensionsKt.asObservable(filtersRepository.getFilters()),
                        (category, filters) -> {
                            pager.onNext(
                                    getNewPager(
                                            category,
                                            filters.getSortBy(),
                                            filters.getChosenDates(),
                                            filters.getLanguage()
                                    )
                            );
                            return Unit.INSTANCE;
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );

        disposables.add(pager
                .switchMap(Pager::loadNextPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pagingData -> {
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

    void initializeCategory(String category) {
        this.category.onNext(category);
    }

    void onRefresh() {
        category.onNext(category.getValue());
    }

    void onLoadNextPage() {
        pager.onNext(pager.getValue());
    }

    void onArticleProfile(Article article) {
        router.onArticleProfile(article);
    }
}
