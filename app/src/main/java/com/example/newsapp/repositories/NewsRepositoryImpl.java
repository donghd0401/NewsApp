package com.example.newsapp.repositories;
import android.util.Log;

import androidx.core.util.Pair;

import com.example.newsapp.model.News;
import com.example.newsapp.request.ApiService;
import com.example.newsapp.response.NewsSearchResponse;
import com.example.newsapp.utils.Credentials;
import com.example.newsapp.viewmodel.SingleLiveEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Response;

public class NewsRepositoryImpl implements NewsRepository {

    private static NewsRepositoryImpl instance;

    public static NewsRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new NewsRepositoryImpl();
        }
        return instance;
    }
    private final SingleLiveEvent<Resource<List<News>>> mSearchNews;
    private final SingleLiveEvent<Resource<List<News>>> mTrendingNews;
    private final SingleLiveEvent<Resource<List<News>>> mPopularNews;
    private final SingleLiveEvent<Resource<News>> mNewsByID;
    private final List<News> currentSearchResults;
    private final List<News> currentPopular;
    private final List<News> currentTrending;
    private String mQuery;
    private final CompositeDisposable compositeDisposable;
    private RetrieveNewsRunnable retrieveNewsRunnable;
    private RetrieveNews retrieveNews;

    public NewsRepositoryImpl() {
        mSearchNews = new SingleLiveEvent<>();
        mTrendingNews = new SingleLiveEvent<>();
        mPopularNews = new SingleLiveEvent<>();
        currentSearchResults = new ArrayList<>();
        mNewsByID = new SingleLiveEvent<>();
        currentPopular = new ArrayList<>();
        currentTrending = new ArrayList<>();
        compositeDisposable = new CompositeDisposable();
    }
    @Override
    public SingleLiveEvent<Resource<News>> getNewsByID() {
        return mNewsByID;
    }

    @Override
    public SingleLiveEvent<Resource<List<News>>> getSearchNews() {
        return mSearchNews;
    }

    @Override
    public SingleLiveEvent<Resource<List<News>>> getPopularNews() {
        return mPopularNews;
    }

    @Override
    public SingleLiveEvent<Resource<List<News>>> getTrendingNews() {
        return mTrendingNews;
    }

    @Override
    public void getNewsByID(int newsID) {
        Single<Response<News>> newsByID = ApiService.getNewsApi().searchSingleNews(
                newsID,
                Credentials.API_KEY
        );
        compositeDisposable.add(
                newsByID
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .subscribe(
                                response->{
                                    News news = new News();
                                    if (response.isSuccessful()) {
                                        news = response.body();
                                    }
                                    mNewsByID.setValue(Resource.success(news));
                                }, throwable -> {
                                    String errorMessage = throwable.getMessage();
                                    mNewsByID.setValue(Resource.error(errorMessage));
                                }
                        )
        );

    }

    @Override
    public void loadNewsApi() {

        if (retrieveNews == null) {
            retrieveNews = new RetrieveNews();
        }

        Single<Response<NewsSearchResponse>> trendingNewsSingle = retrieveNews.getTrendingNews();
        Single<Response<NewsSearchResponse>> popularNewsSingle = retrieveNews.getPopularNews();

        compositeDisposable.add(
                Flowable.zip(
                                popularNewsSingle.toFlowable(),
                                trendingNewsSingle.toFlowable(),
                                (newsPopular, newsTrending) -> {
                                    List<News> popularResults = new ArrayList<>();
                                    List<News> trendingResults = new ArrayList<>();
                                    if (newsPopular.isSuccessful()) {
                                        for(News i:newsPopular.body().getNews()){
                                            if(i.getImage_url()!=null){
                                                popularResults.add(i);
                                            }
                                        }
                                    }
                                    if (newsTrending.isSuccessful()) {
                                        for(News i:newsTrending.body().getNews()){
                                            if(i.getImage_url()!=null){
                                                trendingResults.add(i);
                                            }
                                        }
                                    }
                                    return new Pair<>(popularResults, trendingResults);
                                }
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .subscribe(
                                pair -> {
                                    List<News> trendingResults = pair.second;
                                    List<News> popularResults = pair.first;
                                    currentTrending.addAll(trendingResults);
                                    currentPopular.addAll(popularResults);
                                    mTrendingNews.postValue(Resource.success(currentTrending));
                                    mPopularNews.postValue(Resource.success(currentPopular));
                                },
                                throwable -> {
                                    // Handle error
                                    String errorMessage;
                                    if (throwable instanceof IOException) {
                                        errorMessage = "Network error!";
                                    } else {
                                        errorMessage = "Error occurred during data retrieval";
                                    }
                                    mPopularNews.postValue(Resource.error(errorMessage));
                                    mTrendingNews.postValue(Resource.error(errorMessage));
                                }
                        )
        );
    }

    @Override
    public void searchNews(String query) {
        mQuery = query;
        Log.e("Debug", "Query in search" + query);

        retrieveNewsRunnable = new RetrieveNewsRunnable(query);
        clearSearchResults();

        Single<Response<NewsSearchResponse>> searchNewsSingle = retrieveNewsRunnable.getSearchNews();
        compositeDisposable.add(
                searchNewsSingle
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .subscribe(
                                response -> {
                                    List<News> searchResults = new ArrayList<>();
                                    if (response.isSuccessful()) {
                                        currentSearchResults.addAll(response.body().getNews());
                                        searchResults.addAll(currentSearchResults);
                                    }
                                    mSearchNews.postValue(Resource.success(searchResults));
                                },
                                throwable -> {
                                    // Handle error
                                    String errorMessage = throwable.getMessage();
                                    mSearchNews.postValue(Resource.error(errorMessage));
                                }
                        )
        );
    }


    private static class RetrieveNewsRunnable {
        private final String query;

        public RetrieveNewsRunnable(String query) {
            this.query = query;
        }

        public Single<Response<NewsSearchResponse>> getSearchNews() {
            return ApiService.getNewsApi().searchNews(
                    query,
                    Credentials.API_KEY
            );
        }
    }

    private static class RetrieveNews {

        public RetrieveNews() {
        }

        public Single<Response<NewsSearchResponse>> getPopularNews() {
            return ApiService.getNewsApi().getPopularNews(
                    Credentials.API_KEY
            );
        }

        public Single<Response<NewsSearchResponse>> getTrendingNews() {
            return ApiService.getNewsApi().getTrendingNews(
                    Credentials.API_KEY
            );
        }
    }
    private void clearSearchResults() {
        currentSearchResults.clear();
    }

}
